package net.kigawa.keruta.api.job.controller

import net.kigawa.keruta.core.usecase.job.JobService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/jobs")
class JobAdminController(private val jobService: JobService) {

    @GetMapping
    fun jobList(model: Model): String {
        model.addAttribute("pageTitle", "Job Management")
        model.addAttribute("jobs", jobService.getAllJobs())
        return "admin/jobs"
    }

    @GetMapping("/{id}")
    fun jobDetails(@PathVariable id: String, model: Model): String {
        try {
            val job = jobService.getJobById(id)
            model.addAttribute("pageTitle", "Job Details")
            model.addAttribute("job", job)
            return "admin/job-details"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/jobs"
        }
    }

    @GetMapping("/task/{taskId}")
    fun jobsByTask(@PathVariable taskId: String, model: Model): String {
        model.addAttribute("pageTitle", "Jobs for Task")
        model.addAttribute("jobs", jobService.getJobsByTaskId(taskId))
        model.addAttribute("taskId", taskId)
        return "admin/jobs"
    }
}