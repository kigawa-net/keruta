package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes environment variables.
 * Responsible for setting up environment variables for containers.
 */
@Component
class KubernetesEnvironmentHandler(
    private val configMapHandler: KubernetesConfigMapHandler
) {
    private val logger = LoggerFactory.getLogger(KubernetesEnvironmentHandler::class.java)

    /**
     * Sets up environment variables for the container.
     *
     * @param container The container to set up environment variables for
     */
    fun setupEnvironmentVariables(container: Container) {
        // Add task metadata environment variables (ConfigMap is optional)
        logger.info("Adding environment variables from task-metadata ConfigMap (optional)")
        val envVars = container.env ?: mutableListOf()

        envVars.add(configMapHandler.createConfigMapEnvVar("KERUTA_REPOSITORY_ID", "task-metadata", "repositoryId"))
        envVars.add(configMapHandler.createConfigMapEnvVar("KERUTA_DOCUMENT_ID", "task-metadata", "documentId"))

        // Add agent-related environment variables
        envVars.add(configMapHandler.createConfigMapEnvVar("KERUTA_AGENT_ID", "task-metadata", "agentId"))
        envVars.add(configMapHandler.createConfigMapEnvVar("KERUTA_AGENT_INSTALL_COMMAND", "task-metadata", "agentInstallCommand"))
        envVars.add(configMapHandler.createConfigMapEnvVar("KERUTA_AGENT_EXECUTE_COMMAND", "task-metadata", "agentExecuteCommand"))

        // Add API endpoint environment variable
        envVars.add(EnvVar("KERUTA_API_ENDPOINT", "http://keruta-api.keruta.svc.cluster.local", null))

        container.env = envVars
    }
}
