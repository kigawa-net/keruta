/**
 * Implementation of the KubernetesService interface using the Fabric8 Kubernetes Client.
 */
package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodBuilder
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeBuilder
import io.fabric8.kubernetes.api.model.VolumeMount
import io.fabric8.kubernetes.api.model.VolumeMountBuilder
import io.fabric8.kubernetes.api.model.batch.v1.Job
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder
import net.kigawa.keruta.core.domain.model.KubernetesConfig
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.usecase.kubernetes.KubernetesService
import net.kigawa.keruta.core.usecase.repository.KubernetesConfigRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class KubernetesServiceImpl(
    private val kubernetesConfigRepository: KubernetesConfigRepository
) : KubernetesService {

    private val logger = LoggerFactory.getLogger(KubernetesServiceImpl::class.java)

    // Lazy initialization of the Kubernetes client
    private val client by lazy {
        try {
            val config = kubernetesConfigRepository.getConfig()
            if (config.enabled) {
                if (config.inCluster) {
                    logger.info("Using in-cluster Kubernetes configuration")
                    KubernetesClientBuilder().build()
                } else if (config.configPath.isNotEmpty()) {
                    logger.info("Using Kubernetes configuration from file: ${config.configPath}")
                    System.setProperty("kubeconfig", config.configPath)
                    KubernetesClientBuilder().build()
                } else {
                    logger.info("Using default Kubernetes configuration")
                    KubernetesClientBuilder().build()
                }
            } else {
                logger.warn("Kubernetes integration is disabled. Enable it by setting keruta.kubernetes.enabled=true")
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes client", e)
            null
        }
    }

    override fun createJob(
        task: Task,
        image: String,
        namespace: String,
        jobName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>,
        repository: Repository?
    ): String {
        val config = kubernetesConfigRepository.getConfig()
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
            val metadata = io.fabric8.kubernetes.api.model.ObjectMeta()
            metadata.name = actualJobName
            metadata.namespace = actualNamespace
            metadata.labels = mapOf(
                "app" to "keruta",
                "task-id" to (task.id ?: "")
            )

            // Create pod template metadata
            val podTemplateMetadata = io.fabric8.kubernetes.api.model.ObjectMeta()
            podTemplateMetadata.labels = mapOf(
                "app" to "keruta",
                "task-id" to (task.id ?: "")
            )

            // Create containers list
            val containers = mutableListOf<io.fabric8.kubernetes.api.model.Container>()

            // Create main container
            val mainContainer = io.fabric8.kubernetes.api.model.Container()
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
            val volumes = mutableListOf<io.fabric8.kubernetes.api.model.Volume>()

            // Add init containers list
            val initContainers = mutableListOf<io.fabric8.kubernetes.api.model.Container>()

            // Add volume for git repository if repository is provided
            if (repository != null) {
                logger.info("Adding init container for git clone: ${repository.url}")

                val repoVolumeName = "repo-volume"
                val repoMountPath = "/repo"

                // Create volume for git repository
                val repoVolume = io.fabric8.kubernetes.api.model.Volume()
                repoVolume.name = repoVolumeName

                // Use PVC if specified in repository
                if (repository.usePvc) {
                    logger.info("Using PVC for repository: ${repository.name}")

                    // Determine PVC name based on parent task
                    val pvcName = if (task.parentId != null) {
                        // Use parent task's PVC if available
                        "git-repo-pvc-${task.parentId}"
                    } else {
                        // Create new PVC for this task
                        "git-repo-pvc-${task.id ?: UUID.randomUUID()}"
                    }

                    // Check if PVC already exists
                    val existingPvc = client!!.persistentVolumeClaims()
                        .inNamespace(actualNamespace)
                        .withName(pvcName)
                        .get()

                    // Create PVC if it doesn't exist
                    if (existingPvc == null && task.parentId == null) {
                        logger.info("Creating new PVC: $pvcName")

                        // Create PVC
                        val pvc = PersistentVolumeClaimBuilder()
                            .withNewMetadata()
                                .withName(pvcName)
                                .withNamespace(actualNamespace)
                                .addToLabels("app", "keruta")
                                .addToLabels("task-id", task.id ?: "")
                            .endMetadata()
                            .withNewSpec()
                                .withAccessModes(repository.pvcAccessMode)
                                .withNewResources()
                                    .addToRequests("storage", Quantity(repository.pvcStorageSize))
                                .endResources()
                            .endSpec()
                            .build()

                        client!!.persistentVolumeClaims()
                            .inNamespace(actualNamespace)
                            .create(pvc)
                    } else if (task.parentId != null) {
                        logger.info("Using parent task's PVC: $pvcName")
                    } else {
                        logger.info("Using existing PVC: $pvcName")
                    }

                    // Set volume to use PVC
                    val pvcSource = io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource()
                    pvcSource.claimName = pvcName
                    repoVolume.persistentVolumeClaim = pvcSource
                } else {
                    // Use EmptyDir if PVC is not specified
                    repoVolume.emptyDir = io.fabric8.kubernetes.api.model.EmptyDirVolumeSource()
                }

                volumes.add(repoVolume)

                // Create init container for git clone
                val gitCloneContainer = io.fabric8.kubernetes.api.model.Container()
                gitCloneContainer.name = "git-clone"
                gitCloneContainer.image = "alpine/git"
                gitCloneContainer.command = listOf("git", "clone", repository.url, repoMountPath)

                // Add volume mount to git clone container
                val gitCloneVolumeMount = io.fabric8.kubernetes.api.model.VolumeMount()
                gitCloneVolumeMount.name = repoVolumeName
                gitCloneVolumeMount.mountPath = repoMountPath
                gitCloneContainer.volumeMounts = listOf(gitCloneVolumeMount)

                initContainers.add(gitCloneContainer)

                // Add volume mount to main container
                val mainContainerVolumeMount = io.fabric8.kubernetes.api.model.VolumeMount()
                mainContainerVolumeMount.name = repoVolumeName
                mainContainerVolumeMount.mountPath = repoMountPath

                // Add volume mount to existing volume mounts or create new list
                if (mainContainer.volumeMounts == null) {
                    mainContainer.volumeMounts = mutableListOf(mainContainerVolumeMount)
                } else {
                    (mainContainer.volumeMounts as MutableList<io.fabric8.kubernetes.api.model.VolumeMount>).add(mainContainerVolumeMount)
                }
            }

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
            val podTemplateSpec = io.fabric8.kubernetes.api.model.PodTemplateSpec()
            podTemplateSpec.metadata = podTemplateMetadata
            podTemplateSpec.spec = podSpec

            // Create job spec
            val jobSpec = io.fabric8.kubernetes.api.model.batch.v1.JobSpec()
            jobSpec.backoffLimit = 4 // Number of retries before considering the job failed
            jobSpec.template = podTemplateSpec

            // Create job
            val job = io.fabric8.kubernetes.api.model.batch.v1.Job()
            job.metadata = metadata
            job.spec = jobSpec

            // Create the job
            val createdJob = client!!.batch().v1().jobs().inNamespace(actualNamespace).create(job)
            logger.info("Created Kubernetes job: ${createdJob.metadata.name} in namespace: ${createdJob.metadata.namespace}")

            return createdJob.metadata.name
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes job", e)
            return "error-${UUID.randomUUID()}"
        }
    }

    override fun createPod(
        task: Task,
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): String {
        logger.warn("createPod is deprecated, using createJob instead")
        return createJob(task, image, namespace, podName, resources, additionalEnv)
    }

    override fun getJobLogs(namespace: String, jobName: String): String {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "Kubernetes integration is disabled"
        }

        logger.info("Getting logs for job: $jobName in namespace: $namespace")

        try {
            // Get the job
            val job = client!!.batch().v1().jobs().inNamespace(namespace).withName(jobName).get()
            if (job == null) {
                logger.warn("Job not found: $jobName in namespace: $namespace")
                return "Job not found"
            }

            // Find pods created by this job using label selector
            val labelSelector = "job-name=$jobName"
            val pods = client!!.pods().inNamespace(namespace).withLabelSelector(labelSelector).list().items

            if (pods.isEmpty()) {
                logger.warn("No pods found for job: $jobName in namespace: $namespace")
                return "No pods found for job"
            }

            // Get logs from the first pod (usually there's only one for a job)
            val pod = pods[0]
            val logs = client!!.pods().inNamespace(namespace).withName(pod.metadata.name).getLog()
            return logs
        } catch (e: Exception) {
            logger.error("Failed to get job logs", e)
            return "Error getting logs: ${e.message}"
        }
    }

    override fun getPodLogs(namespace: String, podName: String): String {
        logger.warn("getPodLogs is deprecated, using getJobLogs instead")
        return getJobLogs(namespace, podName)
    }

    override fun deleteJob(namespace: String, jobName: String): Boolean {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return false
        }

        logger.info("Deleting job: $jobName in namespace: $namespace")

        try {
            // The delete() method returns a boolean indicating whether the job was deleted
            val result = client!!.batch().v1().jobs().inNamespace(namespace).withName(jobName).delete()
            // If the result is not null and not empty, the job was deleted
            return result != null && result.isNotEmpty()
        } catch (e: Exception) {
            logger.error("Failed to delete job", e)
            return false
        }
    }

    override fun deletePod(namespace: String, podName: String): Boolean {
        logger.warn("deletePod is deprecated, using deleteJob instead")
        return deleteJob(namespace, podName)
    }

    override fun getJobStatus(namespace: String, jobName: String): String {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "UNKNOWN"
        }

        logger.info("Getting status for job: $jobName in namespace: $namespace")

        try {
            val job = client!!.batch().v1().jobs().inNamespace(namespace).withName(jobName).get()
            if (job == null) {
                logger.warn("Job not found: $jobName in namespace: $namespace")
                return "NOT_FOUND"
            }

            // Check job status conditions
            val conditions = job.status?.conditions
            if (conditions != null && conditions.isNotEmpty()) {
                for (condition in conditions) {
                    if (condition.type == "Failed" && condition.status == "True") {
                        logger.warn("Job $jobName in namespace $namespace has failed")
                        return "FAILED"
                    }
                    if (condition.type == "Complete" && condition.status == "True") {
                        logger.info("Job $jobName in namespace $namespace is complete")
                        return "COMPLETED"
                    }
                }
            }

            // Check if job is active
            val active = job.status?.active
            if (active != null && active > 0) {
                return "ACTIVE"
            }

            // Check if job has succeeded
            val succeeded = job.status?.succeeded
            if (succeeded != null && succeeded > 0) {
                return "SUCCEEDED"
            }

            // Check if job has failed
            val failed = job.status?.failed
            if (failed != null && failed > 0) {
                return "FAILED"
            }

            // Check for CrashLoopBackOff in pods created by this job
            val labelSelector = "job-name=$jobName"
            val pods = client!!.pods().inNamespace(namespace).withLabelSelector(labelSelector).list().items
            for (pod in pods) {
                pod.status.containerStatuses?.forEach { containerStatus ->
                    val waitingState = containerStatus.state?.waiting
                    if (waitingState != null && waitingState.reason == "CrashLoopBackOff") {
                        logger.warn("Pod ${pod.metadata.name} for job $jobName in namespace $namespace is in CrashLoopBackOff state")
                        return "CRASH_LOOP_BACKOFF"
                    }
                }
            }

            return "PENDING"
        } catch (e: Exception) {
            logger.error("Failed to get job status", e)
            return "ERROR"
        }
    }

    override fun getPodStatus(namespace: String, podName: String): String {
        logger.warn("getPodStatus is deprecated, using getJobStatus instead")
        return getJobStatus(namespace, podName)
    }

    override fun getConfig(): KubernetesConfig {
        return kubernetesConfigRepository.getConfig()
    }

    override fun updateConfig(config: KubernetesConfig): KubernetesConfig {
        logger.info("Updating Kubernetes configuration: $config")
        return kubernetesConfigRepository.updateConfig(config)
    }
}
