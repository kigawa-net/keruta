package net.kigawa.keruta.api

import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime
import java.util.UUID

@Controller
@RequestMapping("/admin")
class AdminController {

    private val tasks = mutableListOf<Task>()

    @GetMapping
    fun adminDashboard(model: Model): String {
        model.addAttribute("pageTitle", "Admin Dashboard")
        model.addAttribute("tasks", tasks)
        return "admin/dashboard"
    }

    @GetMapping("/tasks")
    fun taskList(model: Model): String {
        model.addAttribute("pageTitle", "Task Management")
        model.addAttribute("tasks", tasks)
        return "admin/tasks"
    }

    @GetMapping("/tasks/create")
    fun createTaskForm(model: Model): String {
        model.addAttribute("pageTitle", "Create Task")
        model.addAttribute("task", Task(
            title = "",
            description = "",
            priority = 0,
            status = TaskStatus.PENDING
        )
        )
        model.addAttribute("statuses", TaskStatus.entries.toTypedArray())
        return "admin/task-form"
    }

    @PostMapping("/tasks/create")
    fun createTask(@ModelAttribute task: Task): String {
        val newTask = task.copy(
            id = UUID.randomUUID().toString(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        tasks.add(newTask)
        return "redirect:/admin/tasks"
    }

    @GetMapping("/tasks/edit/{id}")
    fun editTaskForm(@PathVariable id: String, model: Model): String {
        val task = tasks.find { it.id == id }
        if (task != null) {
            model.addAttribute("pageTitle", "Edit Task")
            model.addAttribute("task", task)
            model.addAttribute("statuses", TaskStatus.entries.toTypedArray())
            return "admin/task-form"
        }
        return "redirect:/admin/tasks"
    }

    @PostMapping("/tasks/edit/{id}")
    fun updateTask(@PathVariable id: String, @ModelAttribute task: Task): String {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedTask = task.copy(
                id = id,
                updatedAt = LocalDateTime.now()
            )
            tasks[index] = updatedTask
        }
        return "redirect:/admin/tasks"
    }

    @GetMapping("/tasks/delete/{id}")
    fun deleteTask(@PathVariable id: String): String {
        tasks.removeIf { it.id == id }
        return "redirect:/admin/tasks"
    }
}