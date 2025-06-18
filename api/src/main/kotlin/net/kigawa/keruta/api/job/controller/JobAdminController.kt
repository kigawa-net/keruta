package net.kigawa.keruta.api.job.controller

import net.kigawa.keruta.core.usecase.task.TaskService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * This controller handles the admin UI for tasks with pods.
 * It replaces the previous JobAdminController.
 */
@Controller
@RequestMapping("/admin/tasks")
class TaskAdminController(private val taskService: TaskService) {

    @GetMapping("/pods")
    fun taskPodsView(model: Model): String {
        model.addAttribute("pageTitle", "Task Pods Management")
        model.addAttribute("tasks", taskService.getAllTasks().filter { it.podName != null })
        return "admin/task-pods"
    }

    @GetMapping("/{id}/pod")
    fun taskPodDetails(@PathVariable id: String, model: Model): String {
        try {
            val task = taskService.getTaskById(id)
            model.addAttribute("pageTitle", "Task Pod Details")
            model.addAttribute("task", task)
            return "admin/task-pod-details"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/tasks/pods"
        }
    }
}
