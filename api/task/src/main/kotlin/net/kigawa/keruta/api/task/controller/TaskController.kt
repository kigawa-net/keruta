package net.kigawa.keruta.api.task.controller

import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/v1/tasks")
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
}