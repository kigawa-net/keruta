package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes script execution.
 * Responsible for setting up script execution in containers.
 */
@Component
class KubernetesScriptExecutionHandler(
    private val volumeMountHandler: KubernetesVolumeMountHandler,
    private val environmentHandler: KubernetesEnvironmentHandler,
    private val scriptHandler: KubernetesScriptHandler
) {
    private val logger = LoggerFactory.getLogger(KubernetesScriptExecutionHandler::class.java)

    /**
     * Sets up script execution in the main container.
     *
     * @param container The container to set up script execution for
     * @param workVolumeName The name of the work volume
     * @param workMountPath The mount path of the work volume
     * @param createConfigMap Whether to create the ConfigMap if it doesn't exist
     * @param task The task to create metadata for (required if createConfigMap is true)
     * @param repositoryId The repository ID (required if createConfigMap is true)
     * @param documentId The document ID (required if createConfigMap is true)
     * @param agentId The agent ID (required if createConfigMap is true)
     * @param agentInstallCommand The agent install command (required if createConfigMap is true)
     * @param agentExecuteCommand The agent execute command (required if createConfigMap is true)
     */
    fun setupScriptExecution(
        container: Container,
        workVolumeName: String,
        workMountPath: String,
        createConfigMap: Boolean = false,
        task: net.kigawa.keruta.core.domain.model.Task? = null,
        repositoryId: String = "",
        documentId: String = "",
        agentId: String = "",
        agentInstallCommand: String = "",
        agentExecuteCommand: String = ""
    ) {
        logger.info("Setting up script execution in main container")

        // Setup volume mount
        volumeMountHandler.setupVolumeMount(container, workVolumeName, workMountPath)

        // Setup environment variables
        environmentHandler.setupEnvironmentVariables(
            container,
            createConfigMap,
            task,
            repositoryId,
            documentId,
            agentId,
            agentInstallCommand,
            agentExecuteCommand
        )

        // Setup script and command
        scriptHandler.setupScriptAndCommand(container, workMountPath)
    }
}
