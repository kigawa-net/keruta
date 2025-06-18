package net.kigawa.keruta.api.task.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.task.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task", description = "Task management API")
class TaskController(
    private val taskService: TaskService,
) {

    @GetMapping
    fun getAllTasks(): List<Task> {
        return taskService.getAllTasks()
    }

    @PostMapping
    fun createTask(@RequestBody task: Task): Task {
        return taskService.createTask(task)
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: String): ResponseEntity<Task> {
        return try {
            ResponseEntity.ok(taskService.getTaskById(id))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): ResponseEntity<Task> {
        return try {
            ResponseEntity.ok(taskService.updateTask(id, task))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            taskService.deleteTask(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/queue/next")
    @Operation(
        summary = "Get next task from queue", description = "Retrieves the next task from the queue based on priority"
    )
    fun getNextTask(): ResponseEntity<Task> {
        val nextTask = taskService.getNextTaskFromQueue()
        return if (nextTask != null) {
            ResponseEntity.ok(nextTask)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Updates the status of a specific task")
    fun updateTaskStatus(
        @PathVariable id: String,
        @RequestBody status: Map<String, String>,
    ): ResponseEntity<Task> {
        val newStatus = try {
            TaskStatus.valueOf(status["status"] ?: return ResponseEntity.badRequest().build())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }

        return try {
            ResponseEntity.ok(taskService.updateTaskStatus(id, newStatus))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Update task priority", description = "Updates the priority of a specific task")
    fun updateTaskPriority(
        @PathVariable id: String,
        @RequestBody priority: Map<String, Int>,
    ): ResponseEntity<Task> {
        val newPriority = priority["priority"] ?: return ResponseEntity.badRequest().build()

        return try {
            ResponseEntity.ok(taskService.updateTaskPriority(id, newPriority))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieves all tasks with the specified status")
    fun getTasksByStatus(@PathVariable status: String): ResponseEntity<List<Task>> {
        val taskStatus = try {
            TaskStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(taskService.getTasksByStatus(taskStatus))
    }

    @GetMapping("/{id}/logs")
    @Operation(summary = "Get task logs", description = "Retrieves the logs of a specific task")
    fun getTaskLogs(@PathVariable id: String): ResponseEntity<String> {
        return try {
            val task = taskService.getTaskById(id)
            ResponseEntity.ok(task.logs ?: "No logs available")
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/pod")
    @Operation(summary = "Create pod for task", description = "Creates a Kubernetes pod for a specific task")
    fun createPodForTask(
        @PathVariable id: String,
        @RequestBody podConfig: Map<String, Any>
    ): ResponseEntity<Task> {
        val image = podConfig["image"] as? String ?: return ResponseEntity.badRequest().build()
        val namespace = podConfig["namespace"] as? String ?: "default"
        val podName = podConfig["podName"] as? String
        val resources = (podConfig["resources"] as? Map<String, String>)?.let {
            Resources(
                cpu = it["cpu"] ?: "100m",
                memory = it["memory"] ?: "128Mi"
            )
        }
        val additionalEnv = podConfig["additionalEnv"] as? Map<String, String> ?: emptyMap()

        return try {
            ResponseEntity.ok(
                taskService.createPod(
                    taskId = id,
                    image = image,
                    namespace = namespace,
                    podName = podName,
                    resources = resources,
                    additionalEnv = additionalEnv
                )
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/queue/next/pod")
    @Operation(
        summary = "Create pod for next task",
        description = "Creates a Kubernetes pod for the next task in the queue"
    )
    fun createPodForNextTask(@RequestBody podConfig: Map<String, Any>): ResponseEntity<Task> {
        val image = podConfig["image"] as? String ?: return ResponseEntity.badRequest().build()
        val namespace = podConfig["namespace"] as? String ?: "default"
        val podName = podConfig["podName"] as? String
        val resources = (podConfig["resources"] as? Map<String, String>)?.let {
            Resources(
                cpu = it["cpu"] ?: "100m",
                memory = it["memory"] ?: "128Mi"
            )
        }
        val additionalEnv = podConfig["additionalEnv"] as? Map<String, String> ?: emptyMap()

        val task = taskService.createPodForNextTask(
            image = image,
            namespace = namespace,
            podName = podName,
            resources = resources,
            additionalEnv = additionalEnv
        )

        return if (task != null) {
            ResponseEntity.ok(task)
        } else {
            ResponseEntity.noContent().build()
        }
    }
}
