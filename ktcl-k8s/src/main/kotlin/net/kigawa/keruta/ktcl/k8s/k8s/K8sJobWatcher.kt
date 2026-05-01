package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.BatchV1Api
import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.err.K8sErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class K8sJobWatcher(
    apiClient: ApiClient,
    private val config: K8sConfig,
) {
    private val logger = LoggerFactory.get("K8sJobWatcher")
    private val batchApi = BatchV1Api(apiClient)
    private val logWatcher = K8sJobLogWatcher(apiClient, config)

    suspend fun watchJob(
        jobName: String,
        onStatusChange: suspend (JobStatus) -> Unit,
    ): Res<JobStatus, K8sErr> = supervisorScope {
        launch {
            try {
                logWatcher.watchLogs(jobName)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.warning { "Log watch failed for $jobName: ${e.message}" }
            }
        }
        pollUntilComplete(jobName, onStatusChange)
    }

    private suspend fun pollUntilComplete(
        jobName: String,
        onStatusChange: suspend (JobStatus) -> Unit,
    ): Res<JobStatus, K8sErr> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            while (isActive) {
                val status = checkJobStatus(jobName, startTime)
                if (status != null) {
                    logger.info { "Job $jobName finished with status: $status" }
                    onStatusChange(status)
                    return@withContext Res.Ok(status)
                }
                delay(5000)
            }
            Res.Err(K8sErr.JobWatchErr("Watch cancelled", null))
        } catch (e: Exception) {
            logger.info { "Failed to watch Job: $jobName - ${e.message}" }
            Res.Err(K8sErr.JobWatchErr("Watch failed: ${e.message}", e))
        }
    }

    private fun checkJobStatus(jobName: String, startTime: Long): JobStatus? {
        val job = try {
            batchApi.readNamespacedJobStatus(jobName, config.k8sNamespace).execute()
        } catch (e: ApiException) {
            if (e.code in 500..599) {
                logger.warning { "Transient K8s API error (${e.code}) watching $jobName, will retry: ${e.message}" }
                return null
            }
            throw e
        }
        val elapsed = (System.currentTimeMillis() - startTime) / 1000
        return when {
            job.status?.succeeded == 1 -> JobStatus.SUCCEEDED
            job.status?.failed == 1 -> JobStatus.FAILED
            elapsed > config.k8sJobTimeout -> JobStatus.TIMEOUT
            else -> null
        }
    }
}
