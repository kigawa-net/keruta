package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes containers.
 * Responsible for creating and configuring containers for Kubernetes jobs.
 * This class delegates to specialized handlers for different aspects of container configuration.
 */
@Component
class KubernetesContainerHandler(
    private val containerCreator: KubernetesContainerCreator,
    private val scriptExecutionHandler: KubernetesScriptExecutionHandler
) {
    private val logger = LoggerFactory.getLogger(KubernetesContainerHandler::class.java)

    /**
     * Creates the main container for a task.
     *
     * @param task The task to create a container for
     * @param image The Docker image to use
     * @param resources The resource requirements
     * @param additionalEnv Additional environment variables
     * @return The created container
     */
    fun createMainContainer(
        task: Task,
        image: String,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): Container {
        logger.info("Delegating main container creation to KubernetesContainerCreator")
        return containerCreator.createMainContainer(task, image, resources, additionalEnv)
    }

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
        logger.info("Delegating script execution setup to KubernetesScriptExecutionHandler")
        scriptExecutionHandler.setupScriptExecution(container, workVolumeName, workMountPath)
    }
}
