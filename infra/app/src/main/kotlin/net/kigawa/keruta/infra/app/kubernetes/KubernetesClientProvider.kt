package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.client.KubernetesClientBuilder
import net.kigawa.keruta.core.domain.model.KubernetesConfig
import net.kigawa.keruta.core.usecase.repository.KubernetesConfigRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Provider for Kubernetes client.
 * Responsible for creating and managing the Kubernetes client based on configuration.
 */
@Component
class KubernetesClientProvider(
    private val kubernetesConfigRepository: KubernetesConfigRepository
) {
    private val logger = LoggerFactory.getLogger(KubernetesClientProvider::class.java)

    /**
     * Gets the Kubernetes client based on the current configuration.
     * Returns null if Kubernetes integration is disabled or if there's an error creating the client.
     */
    fun getClient() = try {
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

    /**
     * Gets the current Kubernetes configuration.
     */
    fun getConfig(): KubernetesConfig {
        return kubernetesConfigRepository.getConfig()
    }

    /**
     * Updates the Kubernetes configuration.
     */
    fun updateConfig(config: KubernetesConfig): KubernetesConfig {
        logger.info("Updating Kubernetes configuration: $config")
        return kubernetesConfigRepository.updateConfig(config)
    }

    /**
     * Checks if a secret exists in the specified namespace.
     *
     * @param secretName The name of the secret to check
     * @param namespace The namespace to check in
     * @return true if the secret exists, false otherwise
     */
    fun secretExists(secretName: String, namespace: String): Boolean {
        try {
            val client = getClient() ?: return false
            val secret = client.secrets().inNamespace(namespace).withName(secretName).get()
            return secret != null
        } catch (e: Exception) {
            logger.warn("Failed to check if secret $secretName exists in namespace $namespace", e)
            return false
        }
    }
}
