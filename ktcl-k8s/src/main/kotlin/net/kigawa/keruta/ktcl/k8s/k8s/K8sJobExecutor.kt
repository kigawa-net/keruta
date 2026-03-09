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
    ): Res<String, K8sErr> = coroutineScope {
        try {
            val pvcName = "keruta-task-$taskId-pvc"

            // 1. PVCを作成
            val pvcSpec = V1PersistentVolumeClaimSpec()
                .accessModes(listOf("ReadWriteOnce"))
                .resources(
                    V1VolumeResourceRequirements()
                        .requests(mapOf("storage" to Quantity(config.pvcStorageSize)))
                )
            if (config.pvcStorageClassName != null) {
                pvcSpec.storageClassName(config.pvcStorageClassName)
            }
            val pvc = V1PersistentVolumeClaim()
                .metadata(V1ObjectMeta().name(pvcName).namespace(config.k8sNamespace))
                .spec(pvcSpec)
            withContext(Dispatchers.IO) {
                try {
                    coreApi.createNamespacedPersistentVolumeClaim(config.k8sNamespace, pvc).execute()
                    logger.info { "Created PVC: $pvcName" }
                } catch (e: ApiException) {
                    if (e.code == 409) {
                        logger.info { "PVC already exists, reusing: $pvcName" }
                    } else {
                        throw e
                    }
                }
            }

            // 2. Job定義YAMLを読み込み
            val job = templateLoader.loadTemplate(taskId, title, description, gitRepoUrl, githubToken)
            job.metadata?.namespace(config.k8sNamespace)

            // 3. BatchV1Api.createNamespacedJob()でJob作成
            val jobName = withContext(Dispatchers.IO) {
                try {
                    val createdJob = batchApi.createNamespacedJob(config.k8sNamespace, job).execute()
                    createdJob.metadata?.name ?: return@withContext null
                } catch (e: ApiException) {
                    if (e.code == 409) {
                        logger.info { "Job already exists, skipping: keruta-task-$taskId" }
                        "keruta-task-$taskId"
                    } else {
                        throw e
                    }
                }
            } ?: return@coroutineScope Res.Err(K8sErr.JobCreateErr("Job name is null", null))

            logger.info { "Kubernetes Job ready: $jobName" }

            // 4. Job完了まで待機（ログも並行監視）
            val watchResult = jobWatcher.watchJob(jobName) { status ->
                logger.info { "Job $jobName status changed: $status" }
            }
            when (watchResult) {
                is Res.Ok -> logger.info { "Job $jobName completed with status: ${watchResult.value}" }
                is Res.Err -> logger.warning { "Job $jobName watch error: ${watchResult.err}" }
            }

            Res.Ok(jobName)
        } catch (e: Exception) {
            logger.info { "Failed to create Kubernetes Job: ${e.message}" }
            Res.Err(K8sErr.JobCreateErr("Failed to create Job: ${e.message}", e))
        }
    }
}
