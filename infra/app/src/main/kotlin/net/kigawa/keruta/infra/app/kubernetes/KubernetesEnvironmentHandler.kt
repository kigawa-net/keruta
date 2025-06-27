package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.EnvVar
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
     * @param repositoryId The repository ID
     * @param documentId The document ID
     * @param agentId The agent ID
     * @param agentInstallCommand The agent install command
     * @param agentExecuteCommand The agent execute command
     */
    fun setupEnvironmentVariables(
        repositoryId: String = "",
        documentId: String = "",
        agentId: String = "",
        agentInstallCommand: String = "",
        agentExecuteCommand: String = "",
    ): List<EnvVar> {
        // Add task metadata environment variables directly
        logger.info("Adding environment variables directly")
        return listOf(
            EnvVar("KERUTA_REPOSITORY_ID", repositoryId, null),
            EnvVar("KERUTA_DOCUMENT_ID", documentId, null),
            // Add agent-related environment variables
            EnvVar("KERUTA_AGENT_ID", agentId, null),
            EnvVar("KERUTA_AGENT_INSTALL_COMMAND", agentInstallCommand, null),
            EnvVar("KERUTA_AGENT_EXECUTE_COMMAND", agentExecuteCommand, null),

            // Add API endpoint environment variable
            EnvVar("KERUTA_API_ENDPOINT", "http://keruta-api.keruta.svc.cluster.local", null),
        )
    }
}
