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
            // Create a job definition
            val jobBuilder = JobBuilder()
                .withNewMetadata()
                    .withName(actualJobName)
                    .withNamespace(actualNamespace)
                    .addToLabels("app", "keruta")
                    .addToLabels("task-id", task.id ?: "")
                .endMetadata()
                .withNewSpec()
                    .withBackoffLimit(4) // Number of retries before considering the job failed
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", "keruta")
                            .addToLabels("task-id", task.id ?: "")
                        .endMetadata()
                        .withNewSpec()
                            .withRestartPolicy("Never") // Do not restart containers on failure

            // Add volume for git repository if repository is provided
            if (repository != null) {
                logger.info("Adding init container for git clone: ${repository.url}")

                // Add volume for git repository
                jobBuilder.editSpec()
                    .editTemplate()
                        .editSpec()
                            .addNewVolume()
                                .withName("repo-volume")
                                .withNewEmptyDir()
                                .endEmptyDir()
                            .endVolume()
                        .endSpec()
                    .endTemplate()
                .endSpec()

                // Add init container for git clone
                jobBuilder.editSpec()
                    .editTemplate()
                        .editSpec()
                            .addNewInitContainer()
                                .withName("git-clone")
                                .withImage("alpine/git")
                                .withCommand("git", "clone", repository.url, "/repo")
                                .addNewVolumeMount()
                                    .withName("repo-volume")
                                    .withMountPath("/repo")
                                .endVolumeMount()
                            .endInitContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
            }

            // Add main container
            jobBuilder.editSpec()
                .editTemplate()
                    .editSpec()
                        .addNewContainer()
                            .withName("task-container")
                            .withImage(image)
                            .addNewEnv().withName("KERUTA_TASK_ID").withValue(task.id ?: "").endEnv()
                            .addNewEnv().withName("KERUTA_TASK_TITLE").withValue(task.title).endEnv()
                            .addNewEnv().withName("KERUTA_TASK_DESCRIPTION").withValue(task.description ?: "").endEnv()
                            .addNewEnv().withName("KERUTA_TASK_PRIORITY").withValue(task.priority.toString()).endEnv()
                            .addNewEnv().withName("KERUTA_TASK_STATUS").withValue(task.status.name).endEnv()
                            .addNewEnv().withName("KERUTA_TASK_CREATED_AT").withValue(task.createdAt.format(DateTimeFormatter.ISO_DATE_TIME)).endEnv()
                            .addNewEnv().withName("KERUTA_TASK_UPDATED_AT").withValue(task.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME)).endEnv()
                        .endContainer()
                    .endSpec()
                .endTemplate()
            .endSpec()

            // Add volume mount to main container if repository is provided
            if (repository != null) {
                jobBuilder.editSpec()
                    .editTemplate()
                        .editSpec()
                            .editContainer(0)
                                .addNewVolumeMount()
                                    .withName("repo-volume")
                                    .withMountPath("/repo")
                                .endVolumeMount()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
            }

            val job = jobBuilder.build()

            // Add additional environment variables
            additionalEnv.forEach { (key, value) ->
                job.spec.template.spec.containers[0].env.add(io.fabric8.kubernetes.api.model.EnvVar(key, value, null))
            }

            // Add resource requirements if specified
            if (resources != null) {
                val container = job.spec.template.spec.containers[0]
                container.resources = io.fabric8.kubernetes.api.model.ResourceRequirements()
                container.resources.requests = mapOf(
                    "cpu" to io.fabric8.kubernetes.api.model.Quantity(resources.cpu),
                    "memory" to io.fabric8.kubernetes.api.model.Quantity(resources.memory)
                )
                container.resources.limits = mapOf(
                    "cpu" to io.fabric8.kubernetes.api.model.Quantity(resources.cpu),
                    "memory" to io.fabric8.kubernetes.api.model.Quantity(resources.memory)
                )
            }

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
