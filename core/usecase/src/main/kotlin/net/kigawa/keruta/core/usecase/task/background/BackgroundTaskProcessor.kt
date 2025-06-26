package net.kigawa.keruta.core.usecase.task.background

import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.kubernetes.KubernetesService
import net.kigawa.keruta.core.usecase.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.time.Instant

/**
 * Component that processes tasks in the background.
 * It executes registered tasks one by one.
 */
@Component
class BackgroundTaskProcessor(
    private val taskService: TaskService,
    private val config: BackgroundTaskProcessorConfig,
    private val kubernetesService: KubernetesService
) {
    private val logger = LoggerFactory.getLogger(BackgroundTaskProcessor::class.java)
    private val isProcessing = AtomicBoolean(false)
    private val isMonitoring = AtomicBoolean(false)

    // Map to track when pods enter CrashLoopBackOff state
    // Key: podName, Value: timestamp when CrashLoopBackOff was first detected
    private val crashLoopBackOffPods = ConcurrentHashMap<String, Instant>()

    /**
     * Scheduled method that processes the next task in the queue.
     * It ensures that only one task is processed at a time.
     */
    @Scheduled(fixedDelayString = "\${keruta.task.processor.processing-delay:5000}") // Use configured delay or default to 5 seconds
    fun processNextTask() {
        // If already processing a task, skip this run
        if (!isProcessing.compareAndSet(false, true)) {
            logger.debug("Already processing a task, skipping this run")
            return
        }

        // Variable to hold the task being processed
        var currentTask: Task? = null

        try {
            logger.info("Checking for tasks in the queue")

            // Get running tasks
            val runningTasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS)

            // If there are running tasks, wait for them to complete
            if (runningTasks.isNotEmpty()) {
                logger.info("There are ${runningTasks.size} running tasks, waiting for them to complete")
                return
            }

            // Get the next task from the queue before creating a pod
            currentTask = taskService.getNextTaskFromQueue()
            if (currentTask == null) {
                logger.debug("No tasks in the queue")
                return
            }

            // Get Kubernetes config from the database
            val kubernetesConfig = kubernetesService.getConfig()

            // Create a job for the next task in the queue
            val task = taskService.createJobForNextTask(
                image = kubernetesConfig.defaultImage,
                namespace = kubernetesConfig.defaultNamespace,
                resources = null,
                additionalEnv = emptyMap()
            )

            if (task != null) {
                logger.info("Created job for task ${task.id}")
            } else {
                logger.debug("No tasks in the queue")
            }
        } catch (e: Exception) {
            logger.error("Error processing next task", e)

            // Update the status of the task that was being processed
            if (currentTask != null && currentTask.id != null) {
                try {
                    // Update the task status to FAILED
                    val updatedTask = taskService.updateTaskStatus(currentTask.id!!, TaskStatus.FAILED)
                    logger.info("Updated task ${updatedTask.id} status to FAILED due to processing error")

                    // Append error message to task logs
                    taskService.appendTaskLogs(updatedTask.id!!, "Task processing failed: ${e.message}")
                } catch (ex: Exception) {
                    logger.error("Failed to update task status", ex)
                }
            }
        } finally {
            isProcessing.set(false)
        }
    }

    /**
     * Scheduled method that monitors the status of jobs for tasks that are in progress.
     * It checks for jobs in CRASH_LOOP_BACKOFF state and marks tasks as failed if they've been in that state for too long.
     */
    @Scheduled(fixedDelayString = "\${keruta.task.processor.monitoring-delay:10000}") // Use configured delay or default to 10 seconds
    fun monitorJobStatus() {
        // If already monitoring, skip this run
        if (!isMonitoring.compareAndSet(false, true)) {
            logger.debug("Already monitoring job status, skipping this run")
            return
        }

        try {
            logger.debug("Monitoring job status for tasks in progress")

            // Get tasks that are in progress
            val inProgressTasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS)

            // If there are no tasks in progress, clear the map and return
            if (inProgressTasks.isEmpty()) {
                crashLoopBackOffPods.clear()
                return
            }

            // Check the status of each job
            for (task in inProgressTasks) {
                val jobName = task.jobName ?: task.podName // For backward compatibility
                val namespace = task.namespace

                if (jobName == null || namespace == null) {
                    continue
                }

                val jobStatus = kubernetesService.getJobStatus(namespace, jobName)

                if (jobStatus == "CRASH_LOOP_BACKOFF") {
                    logger.warn("Job $jobName for task ${task.id} has pods in CrashLoopBackOff state")

                    // If this is the first time we've seen this job in CrashLoopBackOff state, record the time
                    if (!crashLoopBackOffPods.containsKey(jobName)) {
                        crashLoopBackOffPods[jobName] = Instant.now()
                        logger.info("Started tracking CrashLoopBackOff for job $jobName")
                    }

                    // Check if the job has been in CrashLoopBackOff state for too long
                    val firstDetected = crashLoopBackOffPods[jobName]
                    if (firstDetected != null) {
                        val elapsedMillis = Instant.now().toEpochMilli() - firstDetected.toEpochMilli()

                        if (elapsedMillis > config.crashLoopBackOffTimeout) {
                            logger.error("Job $jobName for task ${task.id} has been in CrashLoopBackOff state for too long (${elapsedMillis}ms), marking task as failed")

                            try {
                                // Update the task status to FAILED
                                val taskId = task.id
                                if (taskId != null) {
                                    val updatedTask = taskService.updateTaskStatus(taskId, TaskStatus.FAILED)
                                    logger.info("Updated task ${updatedTask.id} status to FAILED due to prolonged CrashLoopBackOff")

                                    // Append error message to task logs
                                    taskService.appendTaskLogs(updatedTask.id!!, "Task failed: Job $jobName had pods in CrashLoopBackOff state for too long (${elapsedMillis}ms)")
                                }

                                // Remove the job from the tracking map
                                crashLoopBackOffPods.remove(jobName)
                            } catch (ex: Exception) {
                                logger.error("Failed to update task status", ex)
                            }
                        } else {
                            logger.debug("Job $jobName has been in CrashLoopBackOff state for ${elapsedMillis}ms, will mark as failed after ${config.crashLoopBackOffTimeout}ms")
                        }
                    }
                } else if (jobStatus == "FAILED" || jobStatus == "SUCCEEDED" || jobStatus == "COMPLETED") {
                    // If the job has completed (successfully or not), update the task status
                    logger.info("Job $jobName for task ${task.id} has status: $jobStatus")

                    try {
                        val taskId = task.id
                        if (taskId != null) {
                            val newStatus = if (jobStatus == "FAILED") TaskStatus.FAILED else TaskStatus.COMPLETED
                            val updatedTask = taskService.updateTaskStatus(taskId, newStatus)
                            logger.info("Updated task ${updatedTask.id} status to $newStatus based on job status $jobStatus")

                            // Append status message to task logs
                            taskService.appendTaskLogs(updatedTask.id!!, "Task $newStatus: Job $jobName completed with status $jobStatus")
                        }

                        // Remove the job from tracking if it was being tracked
                        if (crashLoopBackOffPods.containsKey(jobName)) {
                            crashLoopBackOffPods.remove(jobName)
                        }
                    } catch (ex: Exception) {
                        logger.error("Failed to update task status", ex)
                    }
                } else if (crashLoopBackOffPods.containsKey(jobName)) {
                    // If the job is no longer in CrashLoopBackOff state, remove it from the tracking map
                    logger.info("Job $jobName is no longer in CrashLoopBackOff state, current status: $jobStatus")
                    crashLoopBackOffPods.remove(jobName)
                }
            }

            // Clean up any jobs in the tracking map that are no longer associated with in-progress tasks
            val activeJobNames = inProgressTasks.mapNotNull { it.jobName ?: it.podName }.toSet()
            val jobsToRemove = crashLoopBackOffPods.keys().toList().filter { !activeJobNames.contains(it) }

            for (jobName in jobsToRemove) {
                logger.info("Removing job $jobName from CrashLoopBackOff tracking as it's no longer associated with an in-progress task")
                crashLoopBackOffPods.remove(jobName)
            }
        } catch (e: Exception) {
            logger.error("Error monitoring pod status", e)
        } finally {
            isMonitoring.set(false)
        }
    }
}
