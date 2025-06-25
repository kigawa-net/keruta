package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.EnvVarSource
import io.fabric8.kubernetes.api.model.ResourceRequirements
import io.fabric8.kubernetes.api.model.Quantity
import io.fabric8.kubernetes.api.model.VolumeMount
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
class KubernetesContainerHandler(
    private val clientProvider: KubernetesClientProvider
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
        setupVolumeMount(container, workVolumeName, workMountPath)

        // Setup environment variables
        setupEnvironmentVariables(container)

        // Setup script and command
        setupScriptAndCommand(container, workMountPath)
    }

    /**
     * Sets up volume mount for the container.
     *
     * @param container The container to set up volume mounts for
     * @param workVolumeName The name of the work volume
     * @param workMountPath The mount path of the work volume
     */
    private fun setupVolumeMount(container: Container, workVolumeName: String, workMountPath: String) {
        // Check if a volume mount with the same path already exists
        val volumeMounts = container.volumeMounts ?: mutableListOf()
        val existingMount = volumeMounts.find { it.mountPath == workMountPath }

        if (existingMount != null) {
            logger.info("Volume mount with path $workMountPath already exists, skipping")
            return
        }

        // Create volume mount for work directory
        val workVolumeMount = VolumeMount()
        workVolumeMount.name = workVolumeName
        workVolumeMount.mountPath = workMountPath

        // Add volume mount to container
        volumeMounts.add(workVolumeMount)
        container.volumeMounts = volumeMounts
    }

    /**
     * Sets up environment variables for the container.
     *
     * @param container The container to set up environment variables for
     */
    private fun setupEnvironmentVariables(container: Container) {
        // Add task metadata environment variables (ConfigMap is optional)
        logger.info("Adding environment variables from task-metadata ConfigMap (optional)")
        val envVars = container.env ?: mutableListOf()

        envVars.add(createConfigMapEnvVar("KERUTA_REPOSITORY_ID", "task-metadata", "repositoryId"))
        envVars.add(createConfigMapEnvVar("KERUTA_DOCUMENT_ID", "task-metadata", "documentId"))

        // Add API endpoint environment variable
        envVars.add(EnvVar("KERUTA_API_ENDPOINT", "http://keruta-api.keruta.svc.cluster.local", null))

        container.env = envVars
    }

    /**
     * Sets up script and command for the container.
     *
     * @param container The container to set up script and command for
     * @param workMountPath The mount path of the work volume
     */
    private fun setupScriptAndCommand(container: Container, workMountPath: String) {
        // Create setup script
        val setupScript = createSetupScript()

        // Get original command and args
        val originalCommand = container.command
        val originalArgs = container.args

        // Set working directory
        container.workingDir = workMountPath

        // Create a wrapper script that runs the setup script and then the original command
        val wrapperScript = mutableListOf(
            "#!/bin/sh",
            "set -e",
            "",
            "# Run setup script",
            setupScript.joinToString("\n"),
            "",
            "# Run original command"
        )

        if (originalCommand != null && originalCommand.isNotEmpty()) {
            // If original command exists, run it
            wrapperScript.add("exec ${originalCommand.joinToString(" ")} ${originalArgs?.joinToString(" ") ?: ""}")
        } else {
            // If no original command, use a default command
            wrapperScript.add("exec /bin/sh -c \"${originalArgs?.joinToString(" ") ?: "echo 'No command specified'"}\"")
        }

        // Set command and args for container
        container.command = listOf("/bin/sh", "-c")
        container.args = listOf(wrapperScript.joinToString("\n"))
    }

    /**
     * Creates the setup script for the container.
     *
     * @return The setup script as a list of strings
     */
    private fun createSetupScript(): List<String> {
        return listOf(
            "# Install curl if not already installed",
            "if ! command -v curl > /dev/null; then",
            "  if command -v apt-get > /dev/null; then",
            "    apt-get update && apt-get install -y --no-install-recommends curl && apt-get clean && rm -rf /var/lib/apt/lists/*",
            "  elif command -v apk > /dev/null; then",
            "    apk add --no-cache curl",
            "  elif command -v yum > /dev/null; then",
            "    yum install -y curl && yum clean all",
            "  else",
            "    echo 'Warning: curl not found and could not be installed'",
            "  fi",
            "fi",
            "",
            "# Install Java if not already installed",
            "if ! command -v java > /dev/null; then",
            "  echo 'Java not found, attempting to install...'",
            "  if command -v apt-get > /dev/null; then",
            "    apt-get update && apt-get install -y --no-install-recommends default-jre && apt-get clean && rm -rf /var/lib/apt/lists/*",
            "    export JAVA_HOME=/usr/lib/jvm/default-java",
            "  elif command -v apk > /dev/null; then",
            "    apk add --no-cache openjdk11-jre",
            "    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk",
            "  elif command -v yum > /dev/null; then",
            "    yum install -y java-11-openjdk && yum clean all",
            "    export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk",
            "  else",
            "    echo 'Warning: Java not found and could not be installed'",
            "  fi",
            "  # Add JAVA_HOME to environment if it was set",
            "  if [ -n \"\$JAVA_HOME\" ]; then",
            "    echo \"export JAVA_HOME=\$JAVA_HOME\" >> ~/.bashrc",
            "    echo \"export PATH=\$PATH:\$JAVA_HOME/bin\" >> ~/.bashrc",
            "    echo \"Java installed, JAVA_HOME set to \$JAVA_HOME\"",
            "  fi",
            "fi",
            "mkdir -p ./.keruta",
            "",
            "# Check if environment variables are set (they might be empty if ConfigMap doesn't exist)",
            "if [ -z \"\$KERUTA_REPOSITORY_ID\" ]; then",
            "  echo \"KERUTA_REPOSITORY_ID is not set or empty. Checking for local script.\"",
            "  if [ -f ./.keruta/install.sh ]; then",
            "    echo \"Running local setup script...\"",
            "    sh ./.keruta/install.sh",
            "  else",
            "    echo \"No local script found. Skipping repository script.\"",
            "  fi",
            "else",
            "  echo \"Fetching install script for repository: \$KERUTA_REPOSITORY_ID\"",
            "  SCRIPT_URL=\"\${KERUTA_API_ENDPOINT}/api/v1/repositories/\${KERUTA_REPOSITORY_ID}/script\"",
            "",
            "  if curl -sfL -o ./.keruta/install.sh \"\${SCRIPT_URL}\" && [ -s ./.keruta/install.sh ]; then",
            "    echo \"Running downloaded setup script...\"",
            "    chmod +x ./.keruta/install.sh",
            "    ./.keruta/install.sh",
            "  else",
            "    echo \"Install script not found or is empty. Skipping execution.\"",
            "  fi",
            "fi",
            "",
            "# Check if document ID is set",
            "if [ -z \"\$KERUTA_DOCUMENT_ID\" ]; then",
            "  echo \"KERUTA_DOCUMENT_ID is not set or empty. Skipping document download.\"",
            "else",
            "  echo \"Fetching document: \$KERUTA_DOCUMENT_ID\"",
            "  DOC_URL=\"\${KERUTA_API_ENDPOINT}/api/v1/documents/\${KERUTA_DOCUMENT_ID}/content\"",
            "  curl -sfL -o ./.keruta/README.md \"\$DOC_URL\"",
            "fi"
        )
    }

    /**
     * Creates an environment variable from a ConfigMap.
     * If the ConfigMap doesn't exist, it will use an empty string as the default value.
     *
     * @param name The name of the environment variable
     * @param configMapName The name of the ConfigMap
     * @param configMapKey The key in the ConfigMap
     * @return The environment variable
     */
    private fun createConfigMapEnvVar(name: String, configMapName: String, configMapKey: String): EnvVar {
        // Check if the ConfigMap exists in the namespace
        val client = clientProvider.getClient()
        val namespace = clientProvider.getConfig().defaultNamespace

        if (client != null) {
            try {
                val configMap = client.configMaps().inNamespace(namespace).withName(configMapName).get()
                if (configMap != null) {
                    logger.info("ConfigMap $configMapName found, using ConfigMap reference")
                    val envVar = EnvVar()
                    envVar.name = name

                    val valueFrom = EnvVarSource()
                    val configMapKeyRef = io.fabric8.kubernetes.api.model.ConfigMapKeySelector()
                    configMapKeyRef.name = configMapName
                    configMapKeyRef.key = configMapKey
                    configMapKeyRef.optional = true  // Make the ConfigMap reference optional

                    valueFrom.configMapKeyRef = configMapKeyRef
                    envVar.valueFrom = valueFrom

                    return envVar
                }
            } catch (e: Exception) {
                logger.warn("Error checking for ConfigMap $configMapName: ${e.message}")
            }
        }

        // If ConfigMap doesn't exist or there was an error, use an empty string as the default value
        logger.info("ConfigMap $configMapName not found, using empty string as default value")
        return EnvVar(name, "", null)
    }
}
