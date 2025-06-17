/**
 * Service interface for Job operations.
 */
package net.kigawa.keruta.core.usecase.job

import net.kigawa.keruta.core.domain.model.Job
import net.kigawa.keruta.core.domain.model.JobStatus
import net.kigawa.keruta.core.domain.model.Task

interface JobService {
    /**
     * Gets all jobs.
     *
     * @return List of all jobs
     */
    fun getAllJobs(): List<Job>
    
    /**
     * Gets a job by its ID.
     *
     * @param id The ID of the job to get
     * @return The job if found
     * @throws NoSuchElementException if the job is not found
     */
    fun getJobById(id: String): Job
    
    /**
     * Gets jobs by task ID.
     *
     * @param taskId The ID of the task
     * @return List of jobs associated with the task
     */
    fun getJobsByTaskId(taskId: String): List<Job>
    
    /**
     * Creates a new job for a task.
     *
     * @param task The task to create a job for
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace (optional)
     * @param podName The name of the pod (optional)
     * @param resources The resource requirements (optional)
     * @param additionalEnv Additional environment variables (optional)
     * @return The created job
     */
    fun createJob(
        task: Task,
        image: String,
        namespace: String = "default",
        podName: String? = null,
        resources: net.kigawa.keruta.core.domain.model.Resources? = null,
        additionalEnv: Map<String, String> = emptyMap()
    ): Job
    
    /**
     * Creates a Kubernetes pod for a job.
     *
     * @param jobId The ID of the job
     * @return The updated job with pod information
     * @throws NoSuchElementException if the job is not found
     */
    fun createPod(jobId: String): Job
    
    /**
     * Updates the status of a job.
     *
     * @param id The ID of the job to update
     * @param status The new status
     * @return The updated job
     * @throws NoSuchElementException if the job is not found
     */
    fun updateJobStatus(id: String, status: JobStatus): Job
    
    /**
     * Appends logs to a job.
     *
     * @param id The ID of the job to update
     * @param logs The logs to append
     * @return The updated job
     * @throws NoSuchElementException if the job is not found
     */
    fun appendJobLogs(id: String, logs: String): Job
    
    /**
     * Deletes a job by its ID.
     *
     * @param id The ID of the job to delete
     * @throws NoSuchElementException if the job is not found
     */
    fun deleteJob(id: String)
    
    /**
     * Gets jobs by status.
     *
     * @param status The status to filter by
     * @return List of jobs with the specified status
     */
    fun getJobsByStatus(status: JobStatus): List<Job>
    
    /**
     * Creates a job automatically for the next task in the queue.
     *
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace (optional)
     * @param podName The name of the pod (optional)
     * @param resources The resource requirements (optional)
     * @param additionalEnv Additional environment variables (optional)
     * @return The created job, or null if there are no tasks in the queue
     */
    fun createJobForNextTask(
        image: String,
        namespace: String = "default",
        podName: String? = null,
        resources: net.kigawa.keruta.core.domain.model.Resources? = null,
        additionalEnv: Map<String, String> = emptyMap()
    ): Job?
}