package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.custom.Quantity
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.BatchV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ObjectMeta
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimSpec
import io.kubernetes.client.openapi.models.V1VolumeResourceRequirements

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.err.K8sErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class K8sJobExecutor(
    apiClient: ApiClient,
    private val config: K8sConfig,
    private val templateLoader: JobTemplateLoader,
) {
    private val logger = LoggerFactory.get("K8sJobExecutor")
    private val batchApi = BatchV1Api(apiClient)
    private val coreApi = CoreV1Api(apiClient)

    suspend fun executeJob(
        taskId: Long,
        title: String,
        description: String,
        gitRepoUrl: String,
        githubToken: String,
    ): Res<String, K8sErr> = withContext(Dispatchers.IO) {
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
            coreApi.createNamespacedPersistentVolumeClaim(config.k8sNamespace, pvc).execute()
            logger.info { "Created PVC: $pvcName" }

            // 2. Job定義YAMLを読み込み
            val job = templateLoader.loadTemplate(taskId, title, description, gitRepoUrl, githubToken)

            // 3. BatchV1Api.createNamespacedJob()でJob作成
            val createdJob = batchApi.createNamespacedJob(config.k8sNamespace, job).execute()

            val jobName = createdJob.metadata?.name ?: return@withContext Res.Err(
                K8sErr.JobCreateErr("Job name is null", null)
            )

            logger.info { "Created Kubernetes Job: $jobName" }
            Res.Ok(jobName)
        } catch (e: Exception) {
            logger.info { "Failed to create Kubernetes Job: ${e.message}" }
            Res.Err(K8sErr.JobCreateErr("Failed to create Job: ${e.message}", e))
        }
    }
}