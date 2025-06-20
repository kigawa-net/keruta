package net.kigawa.keruta.core.usecase.task.background

import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.kubernetes.KubernetesService
import net.kigawa.keruta.core.usecase.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

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

        try {
            logger.info("Checking for tasks in the queue")

            // Get running tasks
            val runningTasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS)

            // If there are running tasks, wait for them to complete
            if (runningTasks.isNotEmpty()) {
                logger.info("There are ${runningTasks.size} running tasks, waiting for them to complete")
                return
            }

            // Get Kubernetes config from the database
            val kubernetesConfig = kubernetesService.getConfig()

            // Create a pod for the next task in the queue
            val task = taskService.createPodForNextTask(
                image = kubernetesConfig.defaultImage,
                namespace = kubernetesConfig.processorNamespace,
                resources = null,
                additionalEnv = emptyMap()
            )

            if (task != null) {
                logger.info("Created pod for task ${task.id}")
            } else {
                logger.debug("No tasks in the queue")
            }
        } catch (e: Exception) {
            logger.error("Error processing next task", e)
        } finally {
            isProcessing.set(false)
        }
    }
}
