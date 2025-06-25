package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.Volume
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
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
     * @param additionalEnv Additional environment variables
     * @param repository The Git repository to use
     * @return The name of the created job
     */
    fun createJob(
        task: Task,
        image: String,
        namespace: String,
        jobName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>,
        repository: Repository?,
    ): String {
        val config = clientProvider.getConfig()
        val client = clientProvider.getClient()

        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "kubernetes-disabled"
        }

        logger.info("Creating Kubernetes job for task: ${task.id}")

        val actualNamespace = namespace.ifEmpty { config.defaultNamespace }
        val actualJobName = jobName ?: "keruta-job-${task.id ?: UUID.randomUUID()}"

        try {
            // Create job and pod metadata
            val metadata = metadataHandler.createJobMetadata(task.id, actualJobName, actualNamespace)
            val podTemplateMetadata = metadataHandler.createPodTemplateMetadata(task.id)

            // Create main container
            val mainContainer = containerHandler.createMainContainer(task, image, resources, additionalEnv)

            // Create containers list and add main container
            val containers = mutableListOf(mainContainer)

            // Create volumes list
            val volumes = mutableListOf<Volume>()

            // Add init containers list
            val initContainers = mutableListOf<Container>()

            // Create work volume if not already created for repository
            val workVolumeName = if (repository != null) {
                // Use repository volume if available
                repositoryHandler.setupRepository(
                    task,
                    repository,
                    actualNamespace,
                    volumes,
                    initContainers,
                    mainContainer
                )
                "repo-volume" // Use the volume name from repositoryHandler
            } else {
                // Create a new work volume
                volumeHandler.createWorkVolume(volumes, mainContainer)
            }

            // Set up script execution in the main container
            val workMountPath = if (repository != null) "/repo" else "/work"
            containerHandler.setupScriptExecution(
                mainContainer,
                workVolumeName,
                workMountPath
            )

            // Create pod spec and pod template spec
            val podSpec = podSpecHandler.createPodSpec(containers, volumes, initContainers)
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
