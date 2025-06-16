/**
 * Implementation of the TaskService interface.
 */
package net.kigawa.keruta.core.usecase.task

import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import net.kigawa.keruta.core.usecase.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TaskServiceImpl(private val taskRepository: TaskRepository) : TaskService {
    
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
}