/**
 * Implementation of the TaskService interface.
 * This class combines the previous TaskServiceImpl and JobServiceImpl classes.
 */
package net.kigawa.keruta.core.usecase.task

import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.kubernetes.KubernetesService
import net.kigawa.keruta.core.usecase.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val kubernetesService: KubernetesService
) : TaskService {

    private val logger = LoggerFactory.getLogger(TaskServiceImpl::class.java)

    override fun getAllTasks(): List<Task> {
        return taskRepository.findAll()
    }

    override fun getTaskById(id: String): Task {
        return taskRepository.findById(id) ?: throw NoSuchElementException("Task not found with id: $id")
    }

    override fun createTask(task: Task): Task {
        return taskRepository.save(task)
    }

    override fun updateTask(id: String, task: Task): Task {
        val existingTask = getTaskById(id)
        val updatedTask = task.copy(
            id = existingTask.id,
            createdAt = existingTask.createdAt,
            updatedAt = LocalDateTime.now()
        )
        return taskRepository.save(updatedTask)
    }

    override fun deleteTask(id: String) {
        if (!taskRepository.deleteById(id)) {
            throw NoSuchElementException("Task not found with id: $id")
        }
    }

    override fun getNextTaskFromQueue(): Task? {
        return taskRepository.findNextInQueue()
    }

    override fun updateTaskStatus(id: String, status: TaskStatus): Task {
        val existingTask = getTaskById(id)
        val updatedTask = existingTask.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        return taskRepository.save(updatedTask)
    }

    override fun updateTaskPriority(id: String, priority: Int): Task {
        val existingTask = getTaskById(id)
        val updatedTask = existingTask.copy(
            priority = priority,
            updatedAt = LocalDateTime.now()
        )
        return taskRepository.save(updatedTask)
    }

    override fun getTasksByStatus(status: TaskStatus): List<Task> {
        return taskRepository.findByStatus(status)
    }

    override fun createJob(
        taskId: String,
        image: String,
        namespace: String,
        jobName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Task {
        logger.info("Creating Kubernetes job for task with id: $taskId")

        val task = getTaskById(taskId)
        val actualJobName = jobName ?: "keruta-job-${task.id}"

        try {
            val createdJobName = kubernetesService.createJob(
                task = task,
                image = image,
                namespace = namespace,
                jobName = actualJobName,
                resources = resources,
                additionalEnv = additionalEnv
            )

            val updatedTask = task.copy(
                image = image,
                namespace = namespace,
                jobName = createdJobName,
                podName = createdJobName, // For backward compatibility
                additionalEnv = additionalEnv,
                status = TaskStatus.IN_PROGRESS,
                updatedAt = LocalDateTime.now()
            )

            val savedTask = taskRepository.save(updatedTask)
            logger.info("Kubernetes job created for task with id: $taskId, job name: $createdJobName")

            return savedTask
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes job for task with id: $taskId", e)

            val failedTask = task.copy(
                status = TaskStatus.FAILED,
                updatedAt = LocalDateTime.now(),
                logs = "Failed to create Kubernetes job: ${e.message}"
            )

            return taskRepository.save(failedTask)
        }
    }

    override fun createPod(
        taskId: String,
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Task {
        logger.warn("createPod is deprecated, using createJob instead")
        return createJob(
            taskId = taskId,
            image = image,
            namespace = namespace,
            jobName = podName,
            resources = resources,
            additionalEnv = additionalEnv
        )
    }

    override fun appendTaskLogs(id: String, logs: String): Task {
        logger.info("Appending logs to task with id: $id")

        val task = getTaskById(id)
        val updatedLogs = if (task.logs != null) {
            "${task.logs}\n$logs"
        } else {
            logs
        }

        val updatedTask = task.copy(
            logs = updatedLogs,
            updatedAt = LocalDateTime.now()
        )

        return taskRepository.save(updatedTask)
    }

    override fun createJobForNextTask(
        image: String,
        namespace: String,
        jobName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Task? {
        logger.info("Creating job for next task in queue")

        val nextTask = getNextTaskFromQueue() ?: return null

        return createJob(
            taskId = nextTask.id ?: throw IllegalArgumentException("Task ID cannot be null"),
            image = image,
            namespace = namespace,
            jobName = jobName,
            resources = resources,
            additionalEnv = additionalEnv
        )
    }

    override fun createPodForNextTask(
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Task? {
        logger.warn("createPodForNextTask is deprecated, using createJobForNextTask instead")
        return createJobForNextTask(
            image = image,
            namespace = namespace,
            jobName = podName,
            resources = resources,
            additionalEnv = additionalEnv
        )
    }

    override fun setKubernetesManifest(id: String, manifest: String): Task {
        logger.info("Setting Kubernetes manifest for task with id: $id")

        val task = getTaskById(id)
        val updatedTask = task.copy(
            kubernetesManifest = manifest,
            updatedAt = LocalDateTime.now()
        )

        return taskRepository.save(updatedTask)
    }
}
