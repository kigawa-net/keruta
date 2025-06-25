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
class KubernetesEnvironmentHandler(
    private val configMapHandler: KubernetesConfigMapHandler
) {
    private val logger = LoggerFactory.getLogger(KubernetesEnvironmentHandler::class.java)

    /**
     * Creates a task metadata ConfigMap with the given data.
     *
     * @param task The task to create metadata for
     * @param repositoryId The repository ID
     * @param documentId The document ID
     * @param agentId The agent ID
     * @param agentInstallCommand The agent install command
     * @param agentExecuteCommand The agent execute command
     * @param namespace The namespace to create the ConfigMap in (optional)
     * @return True if the ConfigMap was created successfully, false otherwise
     */
    fun createTaskMetadataConfigMap(
        task: Task,
        repositoryId: String,
        documentId: String,
        agentId: String,
        agentInstallCommand: String,
        agentExecuteCommand: String,
        namespace: String = ""
    ): Boolean {
        logger.info("Creating task-metadata ConfigMap for task: ${task.id}")

        val data = mapOf(
            "repositoryId" to repositoryId,
            "documentId" to documentId,
            "agentId" to agentId,
            "agentInstallCommand" to agentInstallCommand,
            "agentExecuteCommand" to agentExecuteCommand
        )

        val configMap = configMapHandler.createConfigMap("task-metadata", data, namespace)
        return configMap != null
    }

    /**
     * Sets up environment variables for the container.
     *
     * @param container The container to set up environment variables for
     * @param createConfigMap Whether to create the ConfigMap if it doesn't exist
     * @param task The task to create metadata for (required if createConfigMap is true)
     * @param repositoryId The repository ID (required if createConfigMap is true)
     * @param documentId The document ID (required if createConfigMap is true)
     * @param agentId The agent ID (required if createConfigMap is true)
     * @param agentInstallCommand The agent install command (required if createConfigMap is true)
     * @param agentExecuteCommand The agent execute command (required if createConfigMap is true)
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
        // Create the ConfigMap if requested
        if (createConfigMap && task != null) {
            val created = createTaskMetadataConfigMap(
                task,
                repositoryId,
                documentId,
                agentId,
                agentInstallCommand,
                agentExecuteCommand
            )
            if (created) {
                logger.info("Created task-metadata ConfigMap successfully")
            } else {
                logger.warn("Failed to create task-metadata ConfigMap")
            }
        }

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
