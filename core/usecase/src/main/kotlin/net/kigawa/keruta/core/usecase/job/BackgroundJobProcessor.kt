package net.kigawa.keruta.core.usecase.job

import net.kigawa.keruta.core.domain.model.JobStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Component that processes jobs in the background.
 * It executes registered tasks one by one as jobs.
 */
@Component
class BackgroundJobProcessor(
    private val jobService: JobService,
    private val config: BackgroundJobProcessorConfig
) {
    private val logger = LoggerFactory.getLogger(BackgroundJobProcessor::class.java)
    private val isProcessing = AtomicBoolean(false)

    /**
     * Scheduled method that processes the next task in the queue.
     * It ensures that only one task is processed at a time.
     */
    @Scheduled(fixedDelayString = "\${keruta.job.processor.processing-delay:5000}") // Use configured delay or default to 5 seconds
    fun processNextTask() {
        // If already processing a task, skip this run
        if (!isProcessing.compareAndSet(false, true)) {
            logger.debug("Already processing a task, skipping this run")
            return
        }

        try {
            logger.info("Checking for tasks in the queue")

            // Get running jobs
            val runningJobs = jobService.getJobsByStatus(JobStatus.RUNNING)

            // If there are running jobs, wait for them to complete
            if (runningJobs.isNotEmpty()) {
                logger.info("There are ${runningJobs.size} running jobs, waiting for them to complete")
                return
            }

            // Create a job for the next task in the queue
            val job = jobService.createJobForNextTask(
                image = config.defaultImage,
                namespace = config.defaultNamespace,
                resources = null,
                additionalEnv = emptyMap()
            )

            if (job != null) {
                logger.info("Created job ${job.id} for task ${job.taskId}")
            } else {
                logger.debug("No tasks in the queue")
            }
        } catch (e: Exception) {
            logger.error("Error processing next task", e)
        } finally {
            isProcessing.set(false)
        }
    }
}
