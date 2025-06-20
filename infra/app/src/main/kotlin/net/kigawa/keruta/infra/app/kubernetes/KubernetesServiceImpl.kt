/**
 * Implementation of the KubernetesService interface using the Fabric8 Kubernetes Client.
 */
package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodBuilder
import net.kigawa.keruta.core.domain.model.KubernetesConfig
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

    override fun createPod(
        task: Task,
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): String {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "kubernetes-disabled"
        }

        logger.info("Creating Kubernetes pod for task: ${task.id}")

        val actualNamespace = namespace.ifEmpty { config.defaultNamespace }
        val actualPodName = podName ?: "keruta-task-${task.id ?: UUID.randomUUID()}"

        try {
            // Create a simple pod definition
            val pod = PodBuilder()
                .withNewMetadata()
                    .withName(actualPodName)
                    .withNamespace(actualNamespace)
                    .addToLabels("app", "keruta")
                    .addToLabels("task-id", task.id ?: "")
                .endMetadata()
                .withNewSpec()
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
                .build()

            // Add additional environment variables
            additionalEnv.forEach { (key, value) ->
                pod.spec.containers[0].env.add(io.fabric8.kubernetes.api.model.EnvVar(key, value, null))
            }

            // Add resource requirements if specified
            if (resources != null) {
                val container = pod.spec.containers[0]
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

            // Create the pod
            val createdPod = client!!.pods().inNamespace(actualNamespace).create(pod)
            logger.info("Created Kubernetes pod: ${createdPod.metadata.name} in namespace: ${createdPod.metadata.namespace}")

            return createdPod.metadata.name
        } catch (e: Exception) {
            logger.error("Failed to create Kubernetes pod", e)
            return "error-${UUID.randomUUID()}"
        }
    }

    override fun getPodLogs(namespace: String, podName: String): String {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "Kubernetes integration is disabled"
        }

        logger.info("Getting logs for pod: $podName in namespace: $namespace")

        try {
            val pod = client!!.pods().inNamespace(namespace).withName(podName).get()
            if (pod == null) {
                logger.warn("Pod not found: $podName in namespace: $namespace")
                return "Pod not found"
            }

            val logs = client!!.pods().inNamespace(namespace).withName(podName).getLog()
            return logs
        } catch (e: Exception) {
            logger.error("Failed to get pod logs", e)
            return "Error getting logs: ${e.message}"
        }
    }

    override fun deletePod(namespace: String, podName: String): Boolean {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return false
        }

        logger.info("Deleting pod: $podName in namespace: $namespace")

        try {
            // The delete() method returns a boolean indicating whether the pod was deleted
            val result = client!!.pods().inNamespace(namespace).withName(podName).delete()
            // If the result is not null and not empty, the pod was deleted
            return result != null && result.isNotEmpty()
        } catch (e: Exception) {
            logger.error("Failed to delete pod", e)
            return false
        }
    }

    override fun getPodStatus(namespace: String, podName: String): String {
        val config = kubernetesConfigRepository.getConfig()
        if (!config.enabled || client == null) {
            logger.warn("Kubernetes integration is disabled or client is not available")
            return "UNKNOWN"
        }

        logger.info("Getting status for pod: $podName in namespace: $namespace")

        try {
            val pod = client!!.pods().inNamespace(namespace).withName(podName).get()
            if (pod == null) {
                logger.warn("Pod not found: $podName in namespace: $namespace")
                return "NOT_FOUND"
            }

            val phase = pod.status.phase
            return phase ?: "UNKNOWN"
        } catch (e: Exception) {
            logger.error("Failed to get pod status", e)
            return "ERROR"
        }
    }

    override fun getConfig(): KubernetesConfig {
        return kubernetesConfigRepository.getConfig()
    }

    override fun updateConfig(config: KubernetesConfig): KubernetesConfig {
        logger.info("Updating Kubernetes configuration: $config")
        return kubernetesConfigRepository.updateConfig(config)
    }
}