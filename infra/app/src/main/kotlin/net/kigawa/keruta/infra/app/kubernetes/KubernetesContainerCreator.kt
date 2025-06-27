package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.*
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * Creator for Kubernetes containers.
 * Responsible for creating and configuring main containers for Kubernetes jobs.
 */
@Component
class KubernetesContainerCreator {
    private val logger = LoggerFactory.getLogger(KubernetesContainerCreator::class.java)

    /**
     * Creates the main container for a task.
     *
     * @param task The task to create a container for
     * @param image The Docker image to use
     * @param resources The resource requirements
     * @return The created container
     */
    fun createMainContainer(
        task: Task,
        image: String,
        resources: Resources?,
        volumeMounts: List<VolumeMount>,
        envVars: List<EnvVar>,
    ): Container {
        logger.info("Creating main container for task: ${task.id}")

        // Create main container
        val mainContainer = Container()
        mainContainer.name = "task-container"
        mainContainer.image = image

        // Add environment variables to main container
        mainContainer.env = listOf(
            EnvVar("KERUTA_TASK_ID", task.id, null),
            EnvVar("KERUTA_TASK_TITLE", task.title, null),
            EnvVar("KERUTA_TASK_DESCRIPTION", task.description ?: "", null),
            EnvVar("KERUTA_TASK_PRIORITY", task.priority.toString(), null),
            EnvVar("KERUTA_TASK_STATUS", task.status.name, null),
            EnvVar("KERUTA_TASK_CREATED_AT", task.createdAt.format(DateTimeFormatter.ISO_DATE_TIME), null),
            EnvVar("KERUTA_TASK_UPDATED_AT", task.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME), null)
        ) + envVars

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
        mainContainer.volumeMounts = volumeMounts
        return mainContainer
    }
}