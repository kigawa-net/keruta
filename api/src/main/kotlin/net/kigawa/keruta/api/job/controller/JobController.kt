package net.kigawa.keruta.api.job.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.api.job.dto.CreateJobRequest
import net.kigawa.keruta.api.job.dto.JobResponse
import net.kigawa.keruta.core.domain.model.Job
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.usecase.job.JobService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/jobs")
@Tag(name = "Job", description = "Job management API")
class JobController(private val jobService: JobService) {

    @GetMapping
    @Operation(summary = "Get all jobs", description = "Retrieves all jobs in the system")
    fun getAllJobs(): List<JobResponse> {
        return jobService.getAllJobs().map { JobResponse.fromDomain(it) }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID", description = "Retrieves a specific job by its ID")
    fun getJobById(@PathVariable id: String): ResponseEntity<JobResponse> {
        return try {
            val job = jobService.getJobById(id)
            ResponseEntity.ok(JobResponse.fromDomain(job))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get jobs by task ID", description = "Retrieves all jobs associated with a specific task")
    fun getJobsByTaskId(@PathVariable taskId: String): List<JobResponse> {
        return jobService.getJobsByTaskId(taskId).map { JobResponse.fromDomain(it) }
    }


    @GetMapping("/{id}/logs")
    @Operation(summary = "Get job logs", description = "Retrieves the logs of a specific job")
    fun getJobLogs(@PathVariable id: String): ResponseEntity<String> {
        return try {
            val job = jobService.getJobById(id)
            ResponseEntity.ok(job.logs ?: "No logs available")
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job", description = "Deletes a specific job")
    fun deleteJob(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            jobService.deleteJob(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}
