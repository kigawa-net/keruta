package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeMount
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Handler for Git repository setup in Kubernetes jobs.
 * Responsible for setting up volumes, init containers, and volume mounts for Git repositories.
 */
@Component
class KubernetesRepositoryHandler(
    private val clientProvider: KubernetesClientProvider
) {
    private val logger = LoggerFactory.getLogger(KubernetesRepositoryHandler::class.java)

    /**
     * Sets up a Git repository for a Kubernetes job.
     * 
     * @param task The task associated with the job
     * @param repository The Git repository to set up
     * @param namespace The Kubernetes namespace
     * @param volumes The list of volumes to add to
     * @param initContainers The list of init containers to add to
     * @param mainContainer The main container to add volume mounts to
     * @return True if the repository was set up successfully, false otherwise
     */
    fun setupRepository(
        task: Task,
        repository: Repository,
        namespace: String,
        volumes: MutableList<Volume>,
        initContainers: MutableList<Container>,
        mainContainer: Container
    ): Boolean {
        logger.info("Adding init container for git clone: ${repository.url}")

        val repoVolumeName = "repo-volume"
        val repoMountPath = "/repo"

        // Create volume for git repository
        val repoVolume = Volume()
        repoVolume.name = repoVolumeName

        // Use PVC if specified in repository
        if (repository.usePvc) {
            logger.info("Using PVC for repository: ${repository.name}")

            // Determine PVC name based on parent task
            val pvcName = if (task.parentId != null) {
                // Use parent task's PVC if available
                "git-repo-pvc-${task.parentId}"
            } else {
                // Create new PVC for this task
                "git-repo-pvc-${task.id ?: UUID.randomUUID()}"
            }

            val client = clientProvider.getClient() ?: return false

            // Check if PVC already exists
            val existingPvc = client.persistentVolumeClaims()
                .inNamespace(namespace)
                .withName(pvcName)
                .get()

            // Create PVC if it doesn't exist
            if (existingPvc == null && task.parentId == null) {
                logger.info("Creating new PVC: $pvcName")

                // Create PVC
                val pvc = PersistentVolumeClaimBuilder()
                    .withNewMetadata()
                        .withName(pvcName)
                        .withNamespace(namespace)
                        .addToLabels("app", "keruta")
                        .addToLabels("task-id", task.id ?: "")
                    .endMetadata()
                    .withNewSpec()
                        .withAccessModes(repository.pvcAccessMode)
                        .withNewResources()
                            .addToRequests("storage", io.fabric8.kubernetes.api.model.Quantity(repository.pvcStorageSize))
                        .endResources()
                    .endSpec()
                    .build()

                client.persistentVolumeClaims()
                    .inNamespace(namespace)
                    .create(pvc)
            } else if (task.parentId != null) {
                logger.info("Using parent task's PVC: $pvcName")
            } else {
                logger.info("Using existing PVC: $pvcName")
            }

            // Set volume to use PVC
            val pvcSource = io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource()
            pvcSource.claimName = pvcName
            repoVolume.persistentVolumeClaim = pvcSource
        } else {
            // Use EmptyDir if PVC is not specified
            repoVolume.emptyDir = io.fabric8.kubernetes.api.model.EmptyDirVolumeSource()
        }

        volumes.add(repoVolume)

        // Create init container for git clone
        val gitCloneContainer = Container()
        gitCloneContainer.name = "git-clone"
        gitCloneContainer.image = "alpine/git"

        // Create a shell script that performs git clone and sets up exclusions
        val gitCloneScript = listOf(
            "set -e",
            "git clone --depth 1 --single-branch ${repository.url} ${repoMountPath}",
            "echo 'Setting up git exclusions'",
            "echo '/.keruta' >> ${repoMountPath}/.git/info/exclude",
            "echo 'Git exclusions configured'"
        )

        gitCloneContainer.command = listOf("/bin/sh", "-c")
        gitCloneContainer.args = listOf(gitCloneScript.joinToString("\n"))

        // Create volume mount for repository
        val gitCloneVolumeMount = VolumeMount()
        gitCloneVolumeMount.name = repoVolumeName
        gitCloneVolumeMount.mountPath = repoMountPath

        // Add environment variables for git configuration
        val gitEnvVars = mutableListOf(
            EnvVar("GIT_TERMINAL_PROMPT", "0", null),  // Disable interactive prompts
            EnvVar("GIT_CONFIG_COUNT", "2", null),
            EnvVar("GIT_CONFIG_KEY_0", "http.connectTimeout", null),
            EnvVar("GIT_CONFIG_VALUE_0", "30", null),
            EnvVar("GIT_CONFIG_KEY_1", "http.lowSpeedLimit", null),
            EnvVar("GIT_CONFIG_VALUE_1", "1000", null)
        )

        val client = clientProvider.getClient() ?: return false

        // Check for git credentials secret
        val secretName = "git-credentials-${repository.id}"
        try {
            val secret = client.secrets().inNamespace(namespace).withName(secretName).get()
            if (secret != null) {
                logger.info("Found git credentials secret: $secretName")

                // Create secret references for username and password
                val usernameEnvVar = EnvVar()
                usernameEnvVar.name = "GIT_USERNAME"
                val usernameSource = io.fabric8.kubernetes.api.model.EnvVarSource()
                val usernameSelector = io.fabric8.kubernetes.api.model.SecretKeySelector()
                usernameSelector.name = secretName
                usernameSelector.key = "username"
                usernameSource.secretKeyRef = usernameSelector
                usernameEnvVar.valueFrom = usernameSource

                val passwordEnvVar = EnvVar()
                passwordEnvVar.name = "GIT_PASSWORD"
                val passwordSource = io.fabric8.kubernetes.api.model.EnvVarSource()
                val passwordSelector = io.fabric8.kubernetes.api.model.SecretKeySelector()
                passwordSelector.name = secretName
                passwordSelector.key = "password"
                passwordSource.secretKeyRef = passwordSelector
                passwordEnvVar.valueFrom = passwordSource

                gitEnvVars.add(usernameEnvVar)
                gitEnvVars.add(passwordEnvVar)

                // Add git config for credential helper using environment variables
                // Update GIT_CONFIG_COUNT to 4 (we're adding 2 more configs)
                gitEnvVars.removeIf { it.name == "GIT_CONFIG_COUNT" }
                gitEnvVars.add(EnvVar("GIT_CONFIG_COUNT", "4", null))

                // Add credential.helper configs
                gitEnvVars.add(EnvVar("GIT_CONFIG_KEY_2", "credential.helper", null))
                gitEnvVars.add(EnvVar("GIT_CONFIG_VALUE_2", "store", null))
                gitEnvVars.add(EnvVar("GIT_CONFIG_KEY_3", "credential.helper", null))
                gitEnvVars.add(EnvVar("GIT_CONFIG_VALUE_3", "cache --timeout=300", null))

                // Add a script to create git credentials file and set up git exclusions
                val setupScript = listOf(
                    "set -e",
                    "echo 'Setting up git credentials'",
                    "mkdir -p /git-credentials",
                    "echo \"https://\$GIT_USERNAME:\$GIT_PASSWORD@github.com\" > /git-credentials/.git-credentials",
                    "git config --global credential.helper 'store --file=/git-credentials/.git-credentials'",
                    "echo 'Git credentials configured'",
                    "git clone ${repository.url} ${repoMountPath}",
                    "echo 'Setting up git exclusions'",
                    "echo '/.keruta' >> ${repoMountPath}/.git/info/exclude",
                    "echo 'Git exclusions configured'"
                )

                // Update the command to use the setup script
                gitCloneContainer.command = listOf("/bin/sh", "-c")
                gitCloneContainer.args = listOf(setupScript.joinToString("\n"))

                // Add volume for git credentials
                val credentialsVolume = Volume()
                credentialsVolume.name = "git-credentials"
                credentialsVolume.emptyDir = io.fabric8.kubernetes.api.model.EmptyDirVolumeSource()
                volumes.add(credentialsVolume)

                // Add volume mount for git credentials
                val credentialsVolumeMount = VolumeMount()
                credentialsVolumeMount.name = "git-credentials"
                credentialsVolumeMount.mountPath = "/git-credentials"

                // Add to existing volume mounts
                val volumeMounts = mutableListOf(gitCloneVolumeMount, credentialsVolumeMount)
                gitCloneContainer.volumeMounts = volumeMounts
            } else {
                // No credentials found, use simple volume mount
                gitCloneContainer.volumeMounts = listOf(gitCloneVolumeMount)
            }
        } catch (e: Exception) {
            logger.warn("Failed to get git credentials secret: $secretName", e)
            // Use simple volume mount in case of error
            gitCloneContainer.volumeMounts = listOf(gitCloneVolumeMount)
        }

        gitCloneContainer.env = gitEnvVars

        initContainers.add(gitCloneContainer)

        // Add volume mount to main container
        val mainContainerVolumeMount = VolumeMount()
        mainContainerVolumeMount.name = repoVolumeName
        mainContainerVolumeMount.mountPath = repoMountPath

        // Add volume mount to existing volume mounts or create new list
        if (mainContainer.volumeMounts == null) {
            mainContainer.volumeMounts = mutableListOf(mainContainerVolumeMount)
        } else {
            (mainContainer.volumeMounts as MutableList<VolumeMount>).add(mainContainerVolumeMount)
        }

        return true
    }
}
