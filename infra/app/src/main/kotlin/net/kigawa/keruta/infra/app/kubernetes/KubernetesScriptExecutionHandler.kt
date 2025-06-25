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
     */
    fun setupScriptExecution(
        container: Container,
        workVolumeName: String,
        workMountPath: String
    ) {
        logger.info("Setting up script execution in main container")

        // Setup volume mount
        volumeMountHandler.setupVolumeMount(container, workVolumeName, workMountPath)

        // Setup environment variables
        environmentHandler.setupEnvironmentVariables(container)

        // Setup script and command
        scriptHandler.setupScriptAndCommand(container, workMountPath)
    }
}