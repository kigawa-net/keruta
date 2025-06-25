package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.Quantity
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * Handler for Kubernetes containers.
 * Responsible for creating and configuring containers for Kubernetes jobs.
 */
@Component
class KubernetesContainerHandler {
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
        logger.info("Creating main container for task: ${task.id}")
        
        // Create main container
        val mainContainer = Container()
        mainContainer.name = "task-container"
        mainContainer.image = image

        // Add environment variables to main container
        val envVars = mutableListOf(
            EnvVar("KERUTA_TASK_ID", task.id ?: "", null),
            EnvVar("KERUTA_TASK_TITLE", task.title, null),
            EnvVar("KERUTA_TASK_DESCRIPTION", task.description ?: "", null),
            EnvVar("KERUTA_TASK_PRIORITY", task.priority.toString(), null),
            EnvVar("KERUTA_TASK_STATUS", task.status.name, null),
            EnvVar("KERUTA_TASK_CREATED_AT", task.createdAt.format(DateTimeFormatter.ISO_DATE_TIME), null),
            EnvVar("KERUTA_TASK_UPDATED_AT", task.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME), null)
        )

        // Add additional environment variables
        additionalEnv.forEach { (key, value) ->
            envVars.add(EnvVar(key, value, null))
        }

        mainContainer.env = envVars

        // Add resource requirements if specified
        if (resources != null) {
            val resourceRequirements = ResourceRequirements()
            resourceRequirements.requests = mapOf(
                "cpu" to Quantity(resources.cpu),
                "memory" to Quantity(resources.memory)
            )
            resourceRequirements.limits = mapOf(
                "cpu" to Quantity(resources.cpu),
                "memory" to Quantity(resources.memory)
            )
            mainContainer.resources = resourceRequirements
        }
        
        return mainContainer
    }
}