package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.BatchV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec
import io.kubernetes.client.openapi.models.V1VolumeResourceRequirements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.err.K8sErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger

class K8sJobExecutor(
    apiClient: ApiClient,
    private val config: K8sConfig,
    private val templateLoader: JobTemplateLoader,
) {
    private val logger = getKogger()
    private val batchApi = BatchV1Api(apiClient)
    private val coreApi = CoreV1Api(apiClient)
    private val jobWatcher = K8sJobWatcher(apiClient, config)

    suspend fun executeJob(
        taskId: Long,
        title: String,
        description: String,
        gitRepoUrl: String,
        githubToken: String,
        userToken: String,
        serverToken: String,
        queueId: Long,
    ): Res<String, K8sErr> = coroutineScope {
        try {
            createPvcIfNotExists("keruta-task-$taskId-pvc")

            val jobName = createJob(taskId, title, description, gitRepoUrl, githubToken, userToken, serverToken, queueId)
                ?: return@coroutineScope Res.Err(K8sErr.JobCreateErr("Job name is null", null))

            logger.info { "Kubernetes Job ready: $jobName" }
            watchAndLog(jobName)
            Res.Ok(jobName)
        } catch (e: Exception) {
            logger.info { "Failed to create Kubernetes Job: ${e.message}" }
            Res.Err(K8sErr.JobCreateErr("Failed to create Job: ${e.message}", e))
        }
    }

    private suspend fun createPvcIfNotExists(pvcName: String) {
        val pvcSpec = V1PersistentVolumeClaimSpec()
            .accessModes(listOf("ReadWriteOnce"))
            .resources(V1VolumeResourceRequirements().requests(mapOf("storage" to Quantity(config.pvcStorageSize))))
        if (config.pvcStorageClassName != null) pvcSpec.storageClassName(config.pvcStorageClassName)
        val pvc = V1PersistentVolumeClaim()
            .metadata(V1ObjectMeta().name(pvcName).namespace(config.k8sNamespace))
            .spec(pvcSpec)
        withContext(Dispatchers.IO) {
            try {
                coreApi.createNamespacedPersistentVolumeClaim(config.k8sNamespace, pvc).execute()
                logger.info { "Created PVC: $pvcName" }
            } catch (e: ApiException) {
                if (e.code == 409) logger.info { "PVC already exists, reusing: $pvcName" }
                else throw e
            }
        }
    }

    private suspend fun createJob(
        taskId: Long,
        title: String,
        description: String,
        gitRepoUrl: String,
        githubToken: String,
        userToken: String,
        serverToken: String,
        queueId: Long,
    ): String? {
        val job = templateLoader.loadTemplate(taskId, title, description, gitRepoUrl, githubToken, userToken, serverToken, queueId, config)
        job.metadata?.namespace(config.k8sNamespace)
        return withContext(Dispatchers.IO) {
            try {
                val createdJob = batchApi.createNamespacedJob(config.k8sNamespace, job).execute()
                createdJob.metadata?.name
            } catch (e: ApiException) {
                if (e.code == 409) {
                    logger.info { "Job already exists, deleting and recreating: keruta-task-$taskId" }
                    batchApi.deleteNamespacedJob("keruta-task-$taskId", config.k8sNamespace)
                        .propagationPolicy("Foreground")
                        .execute()
                    waitForJobDeletion("keruta-task-$taskId")
                    val createdJob = batchApi.createNamespacedJob(config.k8sNamespace, job).execute()
                    createdJob.metadata?.name
                } else {
                    throw e
                }
            }
        }
    }

    private suspend fun waitForJobDeletion(jobName: String) {
        withContext(Dispatchers.IO) {
            repeat(30) {
                try {
                    batchApi.readNamespacedJobStatus(jobName, config.k8sNamespace).execute()
                    delay(2000)
                } catch (e: ApiException) {
                    if (e.code == 404) return@withContext
                    throw e
                }
            }
            logger.warning { "Timed out waiting for job deletion: $jobName" }
        }
    }

    private suspend fun watchAndLog(jobName: String) {
        val watchResult = jobWatcher.watchJob(jobName) { status ->
            logger.info { "Job $jobName status changed: $status" }
        }
        when (watchResult) {
            is Res.Ok -> logger.info { "Job $jobName completed with status: ${watchResult.value}" }
            is Res.Err -> logger.warning { "Job $jobName watch error: ${watchResult.err}" }
        }
    }
}