package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.PodLogs
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import kotlinx.coroutines.*
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
    private val podLogs = PodLogs(apiClient)

    suspend fun watchLogs(jobName: String) = withContext(Dispatchers.IO) {
        val podName = waitForPod(jobName) ?: return@withContext
        val pod = coreApi.readNamespacedPod(podName, config.k8sNamespace).execute()

        val initContainerNames = pod.spec?.initContainers?.mapNotNull { it.name } ?: emptyList()
        val containerNames = pod.spec?.containers?.mapNotNull { it.name } ?: emptyList()

        for (containerName in initContainerNames + containerNames) {
            if (!currentCoroutineContext().isActive) break
            if (hasContainerRun(podName, containerName)) {
                streamContainerLogs(podName, containerName)
            } else {
                logger.info { "[$podName/$containerName] skipping logs: container did not run" }
            }
        }
    }

    private fun hasContainerRun(podName: String, containerName: String): Boolean {
        return try {
            val pod = coreApi.readNamespacedPod(podName, config.k8sNamespace).execute()
            val podPhase = pod.status?.phase
            val allStatuses = (pod.status?.initContainerStatuses ?: emptyList()) +
                (pod.status?.containerStatuses ?: emptyList())
            val status = allStatuses.find { it.name == containerName }
            when {
                status?.state?.running != null -> true
                status?.state?.terminated != null -> true
                podPhase == "Failed" || podPhase == "Succeeded" -> false
                else -> true
            }
        } catch (e: Exception) {
            logger.warning { "Failed to check status for $containerName: ${e.message}" }
            true
        }
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
            } catch (e: Exception) {
                e.printStackTrace()
                Thread.sleep(2000)
            }
        }
        logger.warning { "Failed to stream logs for $podName/$containerName after retries" }
    }
}