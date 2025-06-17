package net.kigawa.keruta.api.task.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.task.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task", description = "Task management API")
class TaskController(private val taskService: TaskService) {

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
    @Operation(summary = "Get next task from queue", description = "Retrieves the next task from the queue based on priority")
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
        @RequestBody status: Map<String, String>
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
        @RequestBody priority: Map<String, Int>
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
}