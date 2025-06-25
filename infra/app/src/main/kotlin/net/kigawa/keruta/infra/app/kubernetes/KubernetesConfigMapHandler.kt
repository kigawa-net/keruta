package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.EnvVarSource
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes ConfigMaps.
 * Responsible for creating environment variables from ConfigMaps.
 */
@Component
class KubernetesConfigMapHandler(
    private val clientProvider: KubernetesClientProvider
) {
    private val logger = LoggerFactory.getLogger(KubernetesConfigMapHandler::class.java)

    /**
     * Creates an environment variable from a ConfigMap.
     * If the ConfigMap doesn't exist, it will use an empty string as the default value.
     *
     * @param name The name of the environment variable
     * @param configMapName The name of the ConfigMap
     * @param configMapKey The key in the ConfigMap
     * @return The environment variable
     */
    fun createConfigMapEnvVar(name: String, configMapName: String, configMapKey: String): EnvVar {
        // Check if the ConfigMap exists in the namespace
        val client = clientProvider.getClient()
        val namespace = clientProvider.getConfig().defaultNamespace

        if (client != null) {
            try {
                val configMap = client.configMaps().inNamespace(namespace).withName(configMapName).get()
                if (configMap != null) {
                    logger.info("ConfigMap $configMapName found, using ConfigMap reference")
                    val envVar = EnvVar()
                    envVar.name = name

                    val valueFrom = EnvVarSource()
                    val configMapKeyRef = io.fabric8.kubernetes.api.model.ConfigMapKeySelector()
                    configMapKeyRef.name = configMapName
                    configMapKeyRef.key = configMapKey
                    configMapKeyRef.optional = true  // Make the ConfigMap reference optional

                    valueFrom.configMapKeyRef = configMapKeyRef
                    envVar.valueFrom = valueFrom

                    return envVar
                }
            } catch (e: Exception) {
                logger.warn("Error checking for ConfigMap $configMapName: ${e.message}")
            }
        }

        // If ConfigMap doesn't exist or there was an error, use an empty string as the default value
        logger.info("ConfigMap $configMapName not found, using empty string as default value")
        return EnvVar(name, "", null)
    }
}