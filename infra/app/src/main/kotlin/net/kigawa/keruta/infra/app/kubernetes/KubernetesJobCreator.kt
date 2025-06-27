package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeMount
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.usecase.agent.AgentService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

/**
 * Creator for Kubernetes jobs.
 * Responsible for creating Kubernetes jobs for tasks.
 */
@Component
class KubernetesJobCreator(
    private val clientProvider: KubernetesClientProvider,
    private val repositoryHandler: KubernetesRepositoryHandler,
    private val metadataHandler: KubernetesMetadataHandler,
    private val containerHandler: KubernetesContainerHandler,
    private val volumeHandler: KubernetesVolumeHandler,
    private val podSpecHandler: KubernetesPodSpecHandler,
    private val jobSpecHandler: KubernetesJobSpecHandler,
    private val agentService: AgentService,
) {
    private val logger = LoggerFactory.getLogger(KubernetesJobCreator::class.java)

    /**
     * Creates a Kubernetes job for a task.
     *
     * @param task The task to create a job for
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace
     * @param jobName The name of the job
     * @param resources The resource requirements
     * @param repository The Git repository to use
     * @return The name of the created job
     */
    fun createJob(
        task: Task,
        image: String,
        namespace: String,
        jobName: String?,
        repository: Repository?,
        pvcName: String,
        resources: Resources?,
    ): String {
        val config = clientProvider.getConfig()
        val client = clientProvider.getClient()

        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "kubernetes-disabled"
        }

        logger.info("Creating Kubernetes job for task: ${task.id}")

        val actualNamespace = namespace.ifEmpty { config.defaultNamespace }
        val actualJobName = jobName ?: "keruta-job-${task.id}"

        try {
            // Create job and pod metadata
            val metadata = metadataHandler.createJobMetadata(task.id, actualJobName, actualNamespace)
            val podTemplateMetadata = metadataHandler.createPodTemplateMetadata(task.id)

            // Create volumes list
            val volumes = mutableListOf<Volume>()

            // Add init containers list
            val initContainers = mutableListOf<Container>()

            val pvcMountPath = "/pvc"
            var volumeMounts = listOf<VolumeMount>()
            // Create work volume if not already created for repository
            val workVolumeName = if (repository != null) {
                // Use repository volume if available
                repositoryHandler.setupRepository(
                    task,
                    repository,
                    actualNamespace, pvcName
                ).also { result ->
                    result.volumeMount?.let { it -> volumeMounts = it }
                    result.gitCloneContainer?.let { element -> initContainers.add(element) }
                    volumes.addAll(result.volumes)
                }
                "repo-volume" // Use the volume name from repositoryHandler
            } else {
                // Mount existing PVC if specified
                logger.info("Mounting existing PVC: $pvcName at path: $pvcMountPath")
                val mountExistingPvcResult = volumeHandler.mountExistingPvc(
                    volumes, pvcName, "pvc-volume", pvcMountPath, volumeMounts
                )
                mountExistingPvcResult.second?.let { volumeMounts += it }
                "pvc-volume" // Use the volume name from volumeHandler
            }

            // Extract metadata for ConfigMap
            val repositoryId = repository?.id ?: task.repositoryId ?: ""
            val documentId = task.documents.firstOrNull()?.id ?: ""
            val agentId = task.agentId ?: ""

            // Get agent install and execute commands if agentId is available
            var agentInstallCommand = ""
            var agentExecuteCommand = ""
            if (agentId.isNotEmpty()) {
                try {
                    val agent = agentService.getAgentById(agentId)
                    agentInstallCommand = agent.installCommand
                    agentExecuteCommand = agent.executeCommand
                    logger.info(
                        "Using agent commands for agent $agentId: install='$agentInstallCommand', execute='$agentExecuteCommand'"
                    )
                } catch (e: Exception) {
                    logger.warn("Failed to get agent $agentId: ${e.message}")
                }
            }

            // Determine the work mount path based on the volume type
            val workMountPath = pvcMountPath

            // Set up script execution with ConfigMap creation
            val setupScriptExecutionResult = containerHandler.setupScriptExecution(
                workVolumeName,
                workMountPath,
                // Create ConfigMap
                repositoryId,
                documentId,
                agentId,
                agentInstallCommand,
                agentExecuteCommand,
                volumeMounts,
                null // No container available at this point
            )
            setupScriptExecutionResult.first?.let { volumeMounts += it }
            val envVars = setupScriptExecutionResult.second

            // Create pod spec and pod template spec
            val podSpec = podSpecHandler.createPodSpec(
                mutableListOf(
                    containerHandler.createMainContainer(task, image, resources, volumeMounts, envVars)
                ), volumes, initContainers
            )
            val podTemplateSpec = podSpecHandler.createPodTemplateSpec(podTemplateMetadata, podSpec)

            // Create job spec and job
            val jobSpec = jobSpecHandler.createJobSpec(podTemplateSpec)
            val job = jobSpecHandler.createJob(metadata, jobSpec)

            // Create the job
            val createdJob = client.batch().v1().jobs().inNamespace(actualNamespace).create(job)
            logger.info(
                "Created Kubernetes job: ${createdJob.metadata.name} in namespace: ${createdJob.metadata.namespace}"
            )

            return createdJob.metadata.name
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes job", e)
            return "error-${UUID.randomUUID()}"
        }
    }
}
