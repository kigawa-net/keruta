package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.kodel.api.log.getKogger
import java.io.BufferedReader
import java.io.InputStreamReader

class K8sJobLogWatcher(
    apiClient: ApiClient,
    private val config: K8sConfig,
) {
    private val logger = getKogger()
    private val coreApi = CoreV1Api(apiClient)

    suspend fun watchLogs(jobName: String) = withContext(Dispatchers.IO) {
        // Podが起動するまで待つ
        val podName = waitForPod(jobName) ?: return@withContext
        val pod = coreApi.readNamespacedPod(podName, config.k8sNamespace).execute()

        val initContainerNames = pod.spec?.initContainers?.map { it.name } ?: emptyList()
        val containerNames = pod.spec?.containers?.map { it.name } ?: emptyList()

        for (containerName in initContainerNames + containerNames) {
            if (!isActive) break
            streamContainerLogs(podName, containerName)
        }
    }

    private suspend fun waitForPod(jobName: String): String? {
        repeat(60) {
            if (!isActive) return null
            val pods = coreApi.listNamespacedPod(config.k8sNamespace)
                .labelSelector("job-name=$jobName")
                .execute()
            val podName = pods.items.firstOrNull()?.metadata?.name
            if (podName != null) return podName
            delay(2000)
        }
        logger.warning { "Timed out waiting for pod of job $jobName" }
        return null
    }

    private fun streamContainerLogs(podName: String, containerName: String) {
        logger.info { "[$podName/$containerName] streaming logs" }
        try {
            // コンテナが起動するまでリトライ
            repeat(30) {
                try {
                    val stream = coreApi.readNamespacedPodLog(podName, config.k8sNamespace)
                        .container(containerName)
                        .follow(true)
                        .execute()
                    BufferedReader(InputStreamReader(stream)).use { reader ->
                        reader.lineSequence().forEach { line ->
                            logger.info { "[$podName/$containerName] $line" }
                        }
                    }
                    return
                } catch (e: Exception) {
                    Thread.sleep(2000)
                }
            }
        } catch (e: Exception) {
            logger.warning { "Failed to stream logs for $podName/$containerName: ${e.message}" }
        }
    }
}
