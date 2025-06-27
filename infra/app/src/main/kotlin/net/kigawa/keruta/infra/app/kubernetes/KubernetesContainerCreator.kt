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
class KubernetesContainerCreator(
    private val clientProvider: KubernetesClientProvider
) {
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

        // Set command and args for a shell script that will download and execute keruta-agent
        mainContainer.command = listOf("/bin/sh", "-c")
        val shellScript = listOf(
            "set -ue",
            "# Download and install keruta-agent if it doesn't exist",
            "if [ ! -f /usr/local/bin/keruta-agent ]; then",
            "    apt update && apt install -y curl",
            "    echo \"keruta-agent not found, downloading from KERUTA_AGENT_LATEST_RELEASE_URL\"",
            "    if [ -z \"\$KERUTA_AGENT_LATEST_RELEASE_URL\" ]; then",
            "        echo \"KERUTA_AGENT_LATEST_RELEASE_URL is not set or empty. Cannot download keruta-agent.\"",
            "        exit 1",
            "    fi",
            "    mkdir -p /usr/local/bin",
            "    curl -sfL -o /usr/local/bin/keruta-agent \"\$KERUTA_AGENT_LATEST_RELEASE_URL\"",
            "    chmod +x /usr/local/bin/keruta-agent",
            "    echo \"keruta-agent downloaded and installed successfully\"",
            "fi",
            "",
            "# Execute keruta-agent",
            "/usr/local/bin/keruta-agent execute --task-id \"\$KERUTA_TASK_ID\" --api-url \"\$KERUTA_API_URL\""
        )
        mainContainer.args = listOf(shellScript.joinToString("\n"))

        // Get the namespace from the client provider's config
        val namespace = clientProvider.getConfig().defaultNamespace

        // Create environment variables list
        val environmentVars = mutableListOf(
            EnvVar("KERUTA_TASK_ID", task.id, null),
            EnvVar("KERUTA_TASK_TITLE", task.title, null),
            EnvVar("KERUTA_TASK_DESCRIPTION", task.description ?: "", null),
            EnvVar("KERUTA_TASK_PRIORITY", task.priority.toString(), null),
            EnvVar("KERUTA_TASK_STATUS", task.status.name, null),
            EnvVar("KERUTA_TASK_CREATED_AT", task.createdAt.format(DateTimeFormatter.ISO_DATE_TIME), null),
            EnvVar("KERUTA_TASK_UPDATED_AT", task.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME), null),
            // Add API URL environment variable
            EnvVar("KERUTA_API_URL", "http://keruta-api.keruta.svc.cluster.local", null)
        )

        // Check if the keruta-api-token secret exists
        val secretName = "keruta-api-token"
        if (clientProvider.secretExists(secretName, namespace)) {
            // Create SecretKeySelector for API token
            val secretKeySelector = SecretKeySelector()
            secretKeySelector.name = secretName
            secretKeySelector.key = "token"

            // Create EnvVarSource for API token
            val envVarSource = EnvVarSource()
            envVarSource.secretKeyRef = secretKeySelector

            // Add API token environment variable
            environmentVars.add(EnvVar("KERUTA_API_TOKEN", null, envVarSource))
        } else {
            logger.warn("Secret '$secretName' not found in namespace '$namespace'. KERUTA_API_TOKEN environment variable will not be set.")
        }

        // Add environment variables to main container
        mainContainer.env = environmentVars + envVars

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
