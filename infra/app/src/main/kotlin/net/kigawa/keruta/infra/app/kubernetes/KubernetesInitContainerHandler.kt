package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.EnvVarSource
import io.fabric8.kubernetes.api.model.SecretKeySelector
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeMount
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for init container setup in Kubernetes jobs.
 * Responsible for setting up init containers for setup script execution and file download.
 */
@Component
class KubernetesInitContainerHandler(
    private val clientProvider: KubernetesClientProvider
) {
    private val logger = LoggerFactory.getLogger(KubernetesInitContainerHandler::class.java)

    /**
     * Sets up init containers for setup script execution and file download.
     *
     * @param task The task associated with the job
     * @param namespace The Kubernetes namespace
     * @param volumes The list of volumes to add to
     * @param initContainers The list of init containers to add to
     * @param workVolumeName The name of the work volume
     * @param workMountPath The mount path of the work volume
     * @return True if the init containers were set up successfully, false otherwise
     */
    fun setupInitContainers(
        task: Task,
        namespace: String,
        volumes: MutableList<Volume>,
        initContainers: MutableList<Container>,
        workVolumeName: String,
        workMountPath: String
    ): Boolean {
        logger.info("Setting up init containers for task: ${task.id}")

        // Create setup container
        val setupContainer = createSetupContainer(workMountPath)

        // Setup volume mounts
        setupVolumeMount(setupContainer, workVolumeName, workMountPath)

        // Setup environment variables
        setupEnvironmentVariables(setupContainer)

        // Setup script and command
        setupScriptAndCommand(setupContainer)

        // Add setup container to init containers
        initContainers.add(setupContainer)

        return true
    }

    /**
     * Creates a setup container for script execution and file download.
     *
     * @param workMountPath The mount path of the work volume
     * @return The created container
     */
    private fun createSetupContainer(workMountPath: String): Container {
        val setupContainer = Container()
        setupContainer.name = "setup"
        setupContainer.image = "ubuntu:latest" // Ubuntu image with curl and sh
        setupContainer.workingDir = workMountPath
        return setupContainer
    }

    /**
     * Sets up volume mount for the container.
     *
     * @param container The container to set up volume mounts for
     * @param workVolumeName The name of the work volume
     * @param workMountPath The mount path of the work volume
     */
    private fun setupVolumeMount(container: Container, workVolumeName: String, workMountPath: String) {
        // Create volume mount for work directory
        val workVolumeMount = VolumeMount()
        workVolumeMount.name = workVolumeName
        workVolumeMount.mountPath = workMountPath

        // Add volume mount to container
        container.volumeMounts = listOf(workVolumeMount)
    }

    /**
     * Sets up environment variables for the container.
     *
     * @param container The container to set up environment variables for
     */
    private fun setupEnvironmentVariables(container: Container) {
        // Create environment variables for setup container
        val setupEnvVars = mutableListOf<EnvVar>()

        // Add task metadata environment variables (ConfigMap is optional)
        logger.info("Adding environment variables from task-metadata ConfigMap (optional)")
        setupEnvVars.add(createConfigMapEnvVar("KERUTA_REPOSITORY_ID", "task-metadata", "repositoryId"))
        setupEnvVars.add(createConfigMapEnvVar("KERUTA_DOCUMENT_ID", "task-metadata", "documentId"))

        // Add API endpoint environment variable
        setupEnvVars.add(EnvVar("KERUTA_API_ENDPOINT", "http://keruta-api.keruta.svc.cluster.local", null))

        // Add environment variables to setup container
        container.env = setupEnvVars
    }

    /**
     * Sets up script and command for the container.
     *
     * @param container The container to set up script and command for
     */
    private fun setupScriptAndCommand(container: Container) {
        // Create setup script
        val setupScript = createSetupScript()

        // Set command and args for setup container
        container.command = listOf("sh", "-c")
        container.args = listOf(setupScript.joinToString("\n"))
    }

    /**
     * Creates the setup script for the container.
     *
     * @return The setup script as a list of strings
     */
    private fun createSetupScript(): List<String> {
        return listOf(
            "set -e",
            "# Install curl if not already installed",
            "apt-get update && apt-get install -y --no-install-recommends curl && apt-get clean && rm -rf /var/lib/apt/lists/*",
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
