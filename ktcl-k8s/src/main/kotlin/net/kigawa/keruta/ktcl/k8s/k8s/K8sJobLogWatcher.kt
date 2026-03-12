package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.PodLogs
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.CoreV1Api
import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.kodel.api.log.getKogger
import java.io.BufferedReader
import java.io.InputStreamReader

private val UNRECOVERABLE_WAITING_REASONS = setOf(
    "ImagePullBackOff", "ErrImagePull", "InvalidImageName",
    "CreateContainerConfigError", "CreateContainerError",
)

class K8sJobLogWatcher(
    apiClient: ApiClient,
    private val config: K8sConfig,
) {
    private val logger = getKogger()
    private val coreApi = CoreV1Api(apiClient)
    private val podLogs = PodLogs(apiClient)

    suspend fun watchLogs(jobName: String) = withContext(Dispatchers.IO) {
        val podName = waitForPod(jobName) ?: return@withContext
        val pod = coreApi.readNamespacedPod(podName, config.k8sNamespace).execute()

        val initContainerNames = pod.spec?.initContainers?.mapNotNull { it.name } ?: emptyList()
        val containerNames = pod.spec?.containers?.mapNotNull { it.name } ?: emptyList()

        for (containerName in initContainerNames + containerNames) {
            if (!currentCoroutineContext().isActive) break
            if (waitForContainerToRun(podName, containerName)) {
                streamContainerLogs(podName, containerName)
            } else {
                logger.info { "[$podName/$containerName] skipping logs: container did not run" }
            }
        }
    }

    private fun waitForContainerToRun(podName: String, containerName: String): Boolean {
        var lastPodPhase: String? = null
        var lastWaitingReason: String? = null
        val maxIterations = (config.k8sJobTimeout / 2).toInt().coerceAtLeast(60)
        repeat(maxIterations) {
            try {
                val pod = coreApi.readNamespacedPod(podName, config.k8sNamespace).execute()
                val podPhase = pod.status?.phase
                val allStatuses = (pod.status?.initContainerStatuses ?: emptyList()) +
                    (pod.status?.containerStatuses ?: emptyList())
                val status = allStatuses.find { it.name == containerName }
                val waitingReason = status?.state?.waiting?.reason
                lastPodPhase = podPhase
                lastWaitingReason = waitingReason
                when {
                    status?.state?.running != null || status?.state?.terminated != null -> return true
                    waitingReason in UNRECOVERABLE_WAITING_REASONS -> {
                        logger.warning { "[$podName/$containerName] container stuck: $waitingReason" }
                        return false
                    }
                    podPhase == "Failed" || podPhase == "Succeeded" -> {
                        logger.info { "[$podName/$containerName] pod phase=$podPhase, waitingReason=$waitingReason, skipping" }
                        return false
                    }
                    else -> Thread.sleep(2000)
                }
            } catch (e: Exception) {
                logger.warning { "Failed to check status for $containerName: ${e.message}" }
                return false
            }
        }
        logger.warning { "[$podName/$containerName] timed out waiting: podPhase=$lastPodPhase, waitingReason=$lastWaitingReason" }
        return false
    }

    private suspend fun waitForPod(jobName: String): String? {
        repeat(60) {
            if (!currentCoroutineContext().isActive) return null
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
        repeat(30) {
            try {
                val stream = podLogs.streamNamespacedPodLog(
                    config.k8sNamespace, podName, containerName
                )
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    reader.lineSequence().forEach { line ->
                        logger.info { "[$podName/$containerName] $line" }
                    }
                }
                return
            } catch (e: ApiException) {
                if (e.code == 400) {
                    logger.warning { "[$podName/$containerName] logs unavailable (400), skipping" }
                    return
                }
                Thread.sleep(2000)
            } catch (e: Exception) {
                e.printStackTrace()
                Thread.sleep(2000)
            }
        }
        logger.warning { "Failed to stream logs for $podName/$containerName after retries" }
    }
}
