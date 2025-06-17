/**
 * Implementation of the JobService interface.
 */
package net.kigawa.keruta.core.usecase.job

import net.kigawa.keruta.core.domain.model.Job
import net.kigawa.keruta.core.domain.model.JobStatus
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.kubernetes.KubernetesService
import net.kigawa.keruta.core.usecase.repository.JobRepository
import net.kigawa.keruta.core.usecase.task.TaskService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    private val taskService: TaskService,
    private val kubernetesService: KubernetesService
) : JobService {

    private val logger = LoggerFactory.getLogger(JobServiceImpl::class.java)

    override fun getAllJobs(): List<Job> {
        logger.info("Getting all jobs")
        return jobRepository.findAll()
    }

    override fun getJobById(id: String): Job {
        logger.info("Getting job with id: $id")
        return jobRepository.findById(id) ?: throw NoSuchElementException("Job not found with id: $id")
    }

    override fun getJobsByTaskId(taskId: String): List<Job> {
        logger.info("Getting jobs for task with id: $taskId")
        return jobRepository.findByTaskId(taskId)
    }

    override fun createJob(
        task: Task,
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Job {
        logger.info("Creating job for task with id: ${task.id}")

        val actualPodName = podName ?: "keruta-task-${task.id}"

        val job = Job(
            id = UUID.randomUUID().toString(),
            taskId = task.id ?: throw IllegalArgumentException("Task ID cannot be null"),
            image = image,
            namespace = namespace,
            podName = actualPodName,
            resources = resources,
            additionalEnv = additionalEnv,
            status = JobStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedJob = jobRepository.save(job)
        logger.info("Job created with id: ${savedJob.id}")

        // Update task status to IN_PROGRESS
        taskService.updateTaskStatus(task.id!!, TaskStatus.IN_PROGRESS)

        return savedJob
    }

    override fun createPod(jobId: String): Job {
        logger.info("Creating Kubernetes pod for job with id: $jobId")

        val job = getJobById(jobId)
        val task = taskService.getTaskById(job.taskId)

        try {
            val podName = kubernetesService.createPod(
                task = task,
                image = job.image,
                namespace = job.namespace,
                podName = job.podName,
                resources = job.resources,
                additionalEnv = job.additionalEnv
            )

            val updatedJob = job.copy(
                podName = podName,
                status = JobStatus.RUNNING,
                updatedAt = LocalDateTime.now()
            )

            val savedJob = jobRepository.save(updatedJob)
            logger.info("Kubernetes pod created for job with id: $jobId, pod name: $podName")

            return savedJob
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes pod for job with id: $jobId", e)

            val failedJob = job.copy(
                status = JobStatus.FAILED,
                updatedAt = LocalDateTime.now(),
                logs = "Failed to create Kubernetes pod: ${e.message}"
            )

            return jobRepository.save(failedJob)
        }
    }

    override fun updateJobStatus(id: String, status: JobStatus): Job {
        logger.info("Updating status of job with id: $id to $status")

        val job = getJobById(id)
        val updatedJob = job.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )

        val savedJob = jobRepository.save(updatedJob)

        // If job is completed or failed, update task status
        if (status == JobStatus.COMPLETED) {
            taskService.updateTaskStatus(job.taskId, TaskStatus.COMPLETED)
        } else if (status == JobStatus.FAILED) {
            taskService.updateTaskStatus(job.taskId, TaskStatus.CANCELLED)
        }

        return savedJob
    }

    override fun appendJobLogs(id: String, logs: String): Job {
        logger.info("Appending logs to job with id: $id")

        val job = getJobById(id)
        val updatedLogs = if (job.logs != null) {
            "${job.logs}\n$logs"
        } else {
            logs
        }

        val updatedJob = job.copy(
            logs = updatedLogs,
            updatedAt = LocalDateTime.now()
        )

        return jobRepository.save(updatedJob)
    }

    override fun deleteJob(id: String) {
        logger.info("Deleting job with id: $id")

        if (!jobRepository.deleteById(id)) {
            throw NoSuchElementException("Job not found with id: $id")
        }
    }

    override fun getJobsByStatus(status: JobStatus): List<Job> {
        logger.info("Getting jobs with status: $status")
        return jobRepository.findByStatus(status)
    }

    override fun createJobForNextTask(
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Job? {
        logger.info("Creating job for next task in queue")

        val nextTask = taskService.getNextTaskFromQueue() ?: return null

        val job = createJob(
            task = nextTask,
            image = image,
            namespace = namespace,
            podName = podName,
            resources = resources,
            additionalEnv = additionalEnv
        )

        return createPod(job.id!!)
    }
}
