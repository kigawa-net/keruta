package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.BatchV1Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.err.K8sErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

enum class JobStatus {
    RUNNING, SUCCEEDED, FAILED, TIMEOUT
}

class K8sJobWatcher(
    private val apiClient: ApiClient,
    private val config: K8sConfig,
) {
    private val logger = LoggerFactory.get("K8sJobWatcher")
    private val batchApi = BatchV1Api(apiClient)

    suspend fun watchJob(
        jobName: String,
        onStatusChange: suspend (JobStatus) -> Unit,
    ): Res<JobStatus, K8sErr> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        try {
            while (isActive) {
                // Job状態を取得
                val job = batchApi.readNamespacedJobStatus(jobName, config.k8sNamespace).execute()

                // ステータスチェック
                val status = when {
                    job.status?.succeeded == 1 -> JobStatus.SUCCEEDED
                    job.status?.failed == 1 -> JobStatus.FAILED
                    else -> {
                        // タイムアウトチェック
                        val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                        if (elapsedSeconds > config.k8sJobTimeout) {
                            JobStatus.TIMEOUT
                        } else {
                            null
                        }
                    }
                }

                if (status != null) {
                    logger.info { "Job $jobName finished with status: $status" }
                    onStatusChange(status)
                    return@withContext Res.Ok(status)
                }

                // 5秒待機
                delay(5000)
            }

            Res.Err(K8sErr.JobWatchErr("Watch cancelled", null))
        } catch (e: Exception) {
            logger.info { "Failed to watch Job: $jobName - ${e.message}" }
            Res.Err(K8sErr.JobWatchErr("Watch failed: ${e.message}", e))
        }
    }
}
