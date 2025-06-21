package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeMount
import io.fabric8.kubernetes.api.model.batch.v1.Job
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Creator for Kubernetes jobs.
 * Responsible for creating Kubernetes jobs for tasks.
 */
@Component
class KubernetesJobCreator(
    private val clientProvider: KubernetesClientProvider,
    private val repositoryHandler: KubernetesRepositoryHandler,
    private val initContainerHandler: KubernetesInitContainerHandler
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
        repository: Repository?
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
            // Create a job definition using direct object creation instead of builder pattern
            // Create metadata
            val metadata = ObjectMeta()
            metadata.name = actualJobName
            metadata.namespace = actualNamespace
            metadata.labels = mapOf(
                "app" to "keruta",
                "task-id" to (task.id ?: "")
            )

            // Create pod template metadata
            val podTemplateMetadata = ObjectMeta()
            podTemplateMetadata.labels = mapOf(
                "app" to "keruta",
                "task-id" to (task.id ?: "")
            )

            // Create containers list
            val containers = mutableListOf<Container>()

            // Create main container
            val mainContainer = Container()
            mainContainer.name = "task-container"
            mainContainer.image = image

            // Add environment variables to main container
            val envVars = mutableListOf(
                EnvVar("KERUTA_TASK_ID", task.id ?: "", null),
                EnvVar("KERUTA_TASK_TITLE", task.title, null),
                EnvVar("KERUTA_TASK_DESCRIPTION", task.description ?: "", null),
                EnvVar("KERUTA_TASK_PRIORITY", task.priority.toString(), null),
                EnvVar("KERUTA_TASK_STATUS", task.status.name, null),
                EnvVar("KERUTA_TASK_CREATED_AT", task.createdAt.format(DateTimeFormatter.ISO_DATE_TIME), null),
                EnvVar("KERUTA_TASK_UPDATED_AT", task.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME), null)
            )

            // Add additional environment variables
            additionalEnv.forEach { (key, value) ->
                envVars.add(EnvVar(key, value, null))
            }

            mainContainer.env = envVars

            // Add resource requirements if specified
            if (resources != null) {
                val resourceRequirements = ResourceRequirements()
                resourceRequirements.requests = mapOf(
                    "cpu" to Quantity(resources.cpu),
                    "memory" to Quantity(resources.memory)
                )
                resourceRequirements.limits = mapOf(
                    "cpu" to Quantity(resources.cpu),
                    "memory" to Quantity(resources.memory)
                )
                mainContainer.resources = resourceRequirements
            }

            // Add containers to list
            containers.add(mainContainer)

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
                val workVolume = Volume()
                workVolume.name = "work-volume"
                workVolume.emptyDir = io.fabric8.kubernetes.api.model.EmptyDirVolumeSource()
                volumes.add(workVolume)

                // Add volume mount to main container
                val workVolumeMount = VolumeMount()
                workVolumeMount.name = "work-volume"
                workVolumeMount.mountPath = "/work"

                // Add volume mount to existing volume mounts or create new list
                if (mainContainer.volumeMounts == null) {
                    mainContainer.volumeMounts = mutableListOf(workVolumeMount)
                } else {
                    (mainContainer.volumeMounts as MutableList<VolumeMount>).add(workVolumeMount)
                }

                "work-volume" // Return the volume name
            }

            // Set up init containers for setup script execution and file download
            val workMountPath = if (repository != null) "/repo" else "/work"
            initContainerHandler.setupInitContainers(
                task,
                actualNamespace,
                volumes,
                initContainers,
                workVolumeName,
                workMountPath
            )

            // Create pod spec
            val podSpec = io.fabric8.kubernetes.api.model.PodSpec()
            podSpec.containers = containers
            podSpec.volumes = volumes
            podSpec.restartPolicy = "Never" // Do not restart containers on failure

            // Add init containers if any
            if (initContainers.isNotEmpty()) {
                podSpec.initContainers = initContainers
            }

            // Create pod template spec
            val podTemplateSpec = PodTemplateSpec()
            podTemplateSpec.metadata = podTemplateMetadata
            podTemplateSpec.spec = podSpec

            // Create job spec
            val jobSpec = JobSpec()
            jobSpec.backoffLimit = 4 // Number of retries before considering the job failed
            jobSpec.template = podTemplateSpec

            // Create job
            val job = Job()
            job.metadata = metadata
            job.spec = jobSpec

            // Create the job
            val createdJob = client.batch().v1().jobs().inNamespace(actualNamespace).create(job)
            logger.info("Created Kubernetes job: ${createdJob.metadata.name} in namespace: ${createdJob.metadata.namespace}")

            return createdJob.metadata.name
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes job", e)
            return "error-${UUID.randomUUID()}"
        }
    }
}
