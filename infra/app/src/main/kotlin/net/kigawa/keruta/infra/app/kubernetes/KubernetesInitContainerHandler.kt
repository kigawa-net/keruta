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

        // Create setup container for script execution and file download
        val setupContainer = Container()
        setupContainer.name = "setup"
        setupContainer.image = "curlimages/curl" // Image with curl and sh
        setupContainer.workingDir = workMountPath

        // Create volume mount for work directory
        val workVolumeMount = VolumeMount()
        workVolumeMount.name = workVolumeName
        workVolumeMount.mountPath = workMountPath

        // Add volume mount to setup container
        setupContainer.volumeMounts = listOf(workVolumeMount)

        // Create environment variables for setup container
        val setupEnvVars = mutableListOf<EnvVar>()

        // Add task metadata environment variables
        setupEnvVars.add(createConfigMapEnvVar("KERUTA_REPOSITORY_ID", "task-metadata", "repositoryId"))
        setupEnvVars.add(createConfigMapEnvVar("KERUTA_DOCUMENT_ID", "task-metadata", "documentId"))

        // Add API endpoint environment variable
        setupEnvVars.add(EnvVar("KERUTA_API_ENDPOINT", "http://keruta-api.keruta.svc.cluster.local", null))

        // Add environment variables to setup container
        setupContainer.env = setupEnvVars

        // Create setup script
        val setupScript = listOf(
            "set -e",
            "mkdir -p ./.keruta",
            "",
            "# APIからインストールスクリプトを取得して実行",
            "if [ -n \"\$KERUTA_REPOSITORY_ID\" ]; then",
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
            "elif [ -f ./.keruta/install.sh ]; then",
            "  echo \"Running local setup script...\"",
            "  sh ./.keruta/install.sh",
            "fi",
            "",
            "# APIからドキュメントを取得",
            "if [ -n \"\$KERUTA_DOCUMENT_ID\" ]; then",
            "  echo \"Fetching document: \$KERUTA_DOCUMENT_ID\"",
            "  DOC_URL=\"\${KERUTA_API_ENDPOINT}/api/v1/documents/\${KERUTA_DOCUMENT_ID}/content\"",
            "  curl -sfL -o ./.keruta/README.md \"\$DOC_URL\"",
            "fi"
        )

        // Set command and args for setup container
        setupContainer.command = listOf("sh", "-c")
        setupContainer.args = listOf(setupScript.joinToString("\n"))

        // Add setup container to init containers
        initContainers.add(setupContainer)

        return true
    }

    /**
     * Creates an environment variable from a ConfigMap.
     *
     * @param name The name of the environment variable
     * @param configMapName The name of the ConfigMap
     * @param configMapKey The key in the ConfigMap
     * @return The environment variable
     */
    private fun createConfigMapEnvVar(name: String, configMapName: String, configMapKey: String): EnvVar {
        val envVar = EnvVar()
        envVar.name = name

        val valueFrom = EnvVarSource()
        val configMapKeyRef = io.fabric8.kubernetes.api.model.ConfigMapKeySelector()
        configMapKeyRef.name = configMapName
        configMapKeyRef.key = configMapKey

        valueFrom.configMapKeyRef = configMapKeyRef
        envVar.valueFrom = valueFrom

        return envVar
    }
}
