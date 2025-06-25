package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes environment variables.
 * Responsible for setting up environment variables for containers.
 */
@Component
class KubernetesEnvironmentHandler {
    private val logger = LoggerFactory.getLogger(KubernetesEnvironmentHandler::class.java)

    /**
     * Sets up environment variables for the container.
     *
     * @param container The container to set up environment variables for
     * @param createConfigMap Whether to create the ConfigMap if it doesn't exist (deprecated, kept for compatibility)
     * @param task The task to create metadata for (not used anymore)
     * @param repositoryId The repository ID
     * @param documentId The document ID
     * @param agentId The agent ID
     * @param agentInstallCommand The agent install command
     * @param agentExecuteCommand The agent execute command
     */
    fun setupEnvironmentVariables(
        container: Container,
        createConfigMap: Boolean = false,
        task: Task? = null,
        repositoryId: String = "",
        documentId: String = "",
        agentId: String = "",
        agentInstallCommand: String = "",
        agentExecuteCommand: String = ""
    ) {
        // Add task metadata environment variables directly
        logger.info("Adding environment variables directly")
        val envVars = container.env ?: mutableListOf()

        // Add repository and document environment variables
        envVars.add(EnvVar("KERUTA_REPOSITORY_ID", repositoryId, null))
        envVars.add(EnvVar("KERUTA_DOCUMENT_ID", documentId, null))

        // Add agent-related environment variables
        envVars.add(EnvVar("KERUTA_AGENT_ID", agentId, null))
        envVars.add(EnvVar("KERUTA_AGENT_INSTALL_COMMAND", agentInstallCommand, null))
        envVars.add(EnvVar("KERUTA_AGENT_EXECUTE_COMMAND", agentExecuteCommand, null))

        // Add API endpoint environment variable
        envVars.add(EnvVar("KERUTA_API_ENDPOINT", "http://keruta-api.keruta.svc.cluster.local", null))

        container.env = envVars
    }
}
