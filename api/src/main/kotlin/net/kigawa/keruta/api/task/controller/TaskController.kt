package net.kigawa.keruta.api.task.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.api.task.dto.TaskResponse
import net.kigawa.keruta.api.task.websocket.TaskLogWebSocketHandler
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.usecase.task.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task", description = "Task management API")
class TaskController(
    private val taskService: TaskService,
    private val taskLogWebSocketHandler: TaskLogWebSocketHandler
) {

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks in the system")
    fun getAllTasks(): List<TaskResponse> {
        return taskService.getAllTasks().map { TaskResponse.fromDomain(it) }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID")
    fun getTaskById(@PathVariable id: String): ResponseEntity<TaskResponse> {
        return try {
            val task = taskService.getTaskById(id)
            ResponseEntity.ok(TaskResponse.fromDomain(task))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieves all tasks with a specific status")
    fun getTasksByStatus(@PathVariable status: String): List<TaskResponse> {
        val taskStatus = try {
            net.kigawa.keruta.core.domain.model.TaskStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            return emptyList()
        }
        return taskService.getTasksByStatus(taskStatus).map { TaskResponse.fromDomain(it) }
    }

    @GetMapping("/{id}/logs")
    @Operation(
        summary = "Get task logs", 
        description = "Retrieves the logs of a specific task. For real-time log streaming, use the WebSocket endpoint at /ws/tasks/{id}"
    )
    fun getTaskLogs(@PathVariable id: String): ResponseEntity<String> {
        return try {
            val task = taskService.getTaskById(id)
            ResponseEntity.ok(task.logs ?: "No logs available")
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/logs/stream")
    @Operation(
        summary = "Stream log update", 
        description = "Sends a log update to all connected WebSocket clients for this task"
    )
    fun streamLogUpdate(
        @PathVariable id: String,
        @RequestParam source: String = "stdout",
        @RequestParam level: String = "INFO",
        @RequestBody logContent: String
    ): ResponseEntity<Void> {
        return try {
            // Verify task exists and append logs to the database
            val task = taskService.getTaskById(id)
            taskService.appendTaskLogs(id, logContent)

            // Send log update to WebSocket clients
            taskLogWebSocketHandler.sendLogUpdate(id, logContent, source, level)

            ResponseEntity.ok().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}/kubernetes-manifest")
    @Operation(summary = "Set Kubernetes manifest", description = "Sets the Kubernetes manifest for a specific task")
    fun setKubernetesManifest(@PathVariable id: String, @RequestBody manifest: String): ResponseEntity<TaskResponse> {
        return try {
            val updatedTask = taskService.setKubernetesManifest(id, manifest)
            ResponseEntity.ok(TaskResponse.fromDomain(updatedTask))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a specific task")
    fun deleteTask(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            taskService.deleteTask(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}
