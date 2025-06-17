package net.kigawa.keruta.api.task.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task", description = "Task management API")
class TaskController {

    private val tasks = mutableListOf<Task>()

    @GetMapping
    fun getAllTasks(): List<Task> {
        return tasks
    }

    @PostMapping
    fun createTask(@RequestBody task: Task): Task {
        val newTask = task.copy(
            id = UUID.randomUUID().toString(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        tasks.add(newTask)
        return newTask
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: String): Task? {
        return tasks.find { it.id == id }
    }

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): Task? {
        val index = tasks.indexOfFirst { it.id == id }
        if (index == -1) return null

        val updatedTask = task.copy(
            id = id,
            updatedAt = LocalDateTime.now()
        )
        tasks[index] = updatedTask
        return updatedTask
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: String): Boolean {
        return tasks.removeIf { it.id == id }
    }

    @GetMapping("/queue/next")
    @Operation(summary = "Get next task from queue", description = "Retrieves the next task from the queue based on priority")
    fun getNextTask(): ResponseEntity<Task> {
        val nextTask = tasks.filter { it.status == TaskStatus.PENDING }
            .maxByOrNull { it.priority }

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
        val taskIndex = tasks.indexOfFirst { it.id == id }
        if (taskIndex == -1) {
            return ResponseEntity.notFound().build()
        }

        val newStatus = try {
            TaskStatus.valueOf(status["status"] ?: return ResponseEntity.badRequest().build())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().build()
        }

        val task = tasks[taskIndex]
        val updatedTask = task.copy(
            status = newStatus,
            updatedAt = LocalDateTime.now()
        )
        tasks[taskIndex] = updatedTask

        return ResponseEntity.ok(updatedTask)
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Update task priority", description = "Updates the priority of a specific task")
    fun updateTaskPriority(
        @PathVariable id: String,
        @RequestBody priority: Map<String, Int>
    ): ResponseEntity<Task> {
        val taskIndex = tasks.indexOfFirst { it.id == id }
        if (taskIndex == -1) {
            return ResponseEntity.notFound().build()
        }

        val newPriority = priority["priority"] ?: return ResponseEntity.badRequest().build()

        val task = tasks[taskIndex]
        val updatedTask = task.copy(
            priority = newPriority,
            updatedAt = LocalDateTime.now()
        )
        tasks[taskIndex] = updatedTask

        return ResponseEntity.ok(updatedTask)
    }
}