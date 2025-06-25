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

        // Create and add repository volume
        val repoVolume = createRepositoryVolume(task, repository, namespace, repoVolumeName)
            ?: return false
        volumes.add(repoVolume)

        // Create git clone container
        val gitCloneContainer = createGitCloneContainer(repository, repoVolumeName, repoMountPath)

        // Setup git environment variables and handle credentials
        val client = clientProvider.getClient() ?: return false
        setupGitCredentials(
            client,
            repository,
            namespace,
            gitCloneContainer,
            repoVolumeName,
            repoMountPath,
            volumes
        )

        // Add the container to the list of init containers
        initContainers.add(gitCloneContainer)

        // Add volume mount to main container
        addVolumeToMainContainer(mainContainer, repoVolumeName, repoMountPath)

        return true
    }

    /**
     * Creates a volume for the repository.
     * 
     * @param task The task associated with the job
     * @param repository The Git repository
     * @param namespace The Kubernetes namespace
     * @param volumeName The name to use for the volume
     * @return The created volume, or null if creation failed
     */
    private fun createRepositoryVolume(
        task: Task,
        repository: Repository,
        namespace: String,
        volumeName: String
    ): Volume? {
        val repoVolume = Volume()
        repoVolume.name = volumeName

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

            val client = clientProvider.getClient() ?: return null

            // Check if PVC already exists
            val existingPvc = client.persistentVolumeClaims()
                .inNamespace(namespace)
                .withName(pvcName)
                .get()

            // Create PVC if it doesn't exist
            if (existingPvc == null && task.parentId == null) {
                createPersistentVolumeClaim(client, task, repository, namespace, pvcName)
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

        return repoVolume
    }

    /**
     * Creates a persistent volume claim for the repository.
     * 
     * @param client The Kubernetes client
     * @param task The task associated with the job
     * @param repository The Git repository
     * @param namespace The Kubernetes namespace
     * @param pvcName The name to use for the PVC
     */
    private fun createPersistentVolumeClaim(
        client: io.fabric8.kubernetes.client.KubernetesClient,
        task: Task,
        repository: Repository,
        namespace: String,
        pvcName: String
    ) {
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
    }

    /**
     * Creates a container for git clone operations.
     * 
     * @param repository The Git repository
     * @param volumeName The name of the volume to mount
     * @param mountPath The path where the volume should be mounted
     * @return The created container
     */
    private fun createGitCloneContainer(
        repository: Repository,
        volumeName: String,
        mountPath: String
    ): Container {
        val gitCloneContainer = Container()
        gitCloneContainer.name = "git-clone"
        gitCloneContainer.image = "alpine/git"

        // Create a shell script that performs git clone and sets up exclusions
        val gitCloneScript = listOf(
            "set -e",
            "git clone --depth 1 --single-branch ${repository.url} $mountPath",
            "echo 'Setting up git exclusions'",
            "echo '/.keruta' >> $mountPath/.git/info/exclude",
            "echo 'Git exclusions configured'"
        )

        gitCloneContainer.command = listOf("/bin/sh", "-c")
        gitCloneContainer.args = listOf(gitCloneScript.joinToString("\n"))

        return gitCloneContainer
    }

    /**
     * Sets up git credentials and environment variables for the container.
     * 
     * @param client The Kubernetes client
     * @param repository The Git repository
     * @param namespace The Kubernetes namespace
     * @param container The container to configure
     * @param volumeName The name of the repository volume
     * @param mountPath The path where the repository volume is mounted
     * @param volumes The list of volumes to add to
     */
    private fun setupGitCredentials(
        client: io.fabric8.kubernetes.client.KubernetesClient,
        repository: Repository,
        namespace: String,
        container: Container,
        volumeName: String,
        mountPath: String,
        volumes: MutableList<Volume>
    ) {
        // Create volume mount for repository
        val gitCloneVolumeMount = VolumeMount()
        gitCloneVolumeMount.name = volumeName
        gitCloneVolumeMount.mountPath = mountPath

        // Add environment variables for git configuration
        val gitEnvVars = createBasicGitEnvVars()

        // Check for git credentials secret
        val secretName = "git-credentials-${repository.id}"
        try {
            val secret = client.secrets().inNamespace(namespace).withName(secretName).get()
            if (secret != null) {
                logger.info("Found git credentials secret: $secretName")

                // Configure container with credentials
                configureContainerWithCredentials(
                    container,
                    repository,
                    secretName,
                    gitEnvVars,
                    gitCloneVolumeMount,
                    mountPath,
                    volumes
                )
            } else {
                // No credentials found, use simple volume mount
                container.volumeMounts = listOf(gitCloneVolumeMount)
            }
        } catch (e: Exception) {
            logger.warn("Failed to get git credentials secret: $secretName", e)
            // Use simple volume mount in case of error
            container.volumeMounts = listOf(gitCloneVolumeMount)
        }

        container.env = gitEnvVars
    }

    /**
     * Creates basic git environment variables.
     * 
     * @return List of environment variables
     */
    private fun createBasicGitEnvVars(): MutableList<EnvVar> {
        return mutableListOf(
            EnvVar("GIT_TERMINAL_PROMPT", "0", null),  // Disable interactive prompts
            EnvVar("GIT_CONFIG_COUNT", "2", null),
            EnvVar("GIT_CONFIG_KEY_0", "http.connectTimeout", null),
            EnvVar("GIT_CONFIG_VALUE_0", "30", null),
            EnvVar("GIT_CONFIG_KEY_1", "http.lowSpeedLimit", null),
            EnvVar("GIT_CONFIG_VALUE_1", "1000", null)
        )
    }

    /**
     * Configures a container with git credentials.
     * 
     * @param container The container to configure
     * @param repository The Git repository
     * @param secretName The name of the secret containing credentials
     * @param gitEnvVars The list of environment variables to add to
     * @param repoVolumeMount The volume mount for the repository
     * @param mountPath The path where the repository volume is mounted
     * @param volumes The list of volumes to add to
     */
    private fun configureContainerWithCredentials(
        container: Container,
        repository: Repository,
        secretName: String,
        gitEnvVars: MutableList<EnvVar>,
        repoVolumeMount: VolumeMount,
        mountPath: String,
        volumes: MutableList<Volume>
    ) {
        // Create secret references for username and password
        val usernameEnvVar = createSecretEnvVar("GIT_USERNAME", secretName, "username")
        val passwordEnvVar = createSecretEnvVar("GIT_PASSWORD", secretName, "password")

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
        val setupScript = createGitSetupScript(repository.url, mountPath)

        // Update the command to use the setup script
        container.command = listOf("/bin/sh", "-c")
        container.args = listOf(setupScript.joinToString("\n"))

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
        val volumeMounts = mutableListOf(repoVolumeMount, credentialsVolumeMount)
        container.volumeMounts = volumeMounts
    }

    /**
     * Creates an environment variable that references a secret.
     * 
     * @param name The name of the environment variable
     * @param secretName The name of the secret
     * @param secretKey The key within the secret
     * @return The created environment variable
     */
    private fun createSecretEnvVar(name: String, secretName: String, secretKey: String): EnvVar {
        val envVar = EnvVar()
        envVar.name = name
        val source = io.fabric8.kubernetes.api.model.EnvVarSource()
        val selector = io.fabric8.kubernetes.api.model.SecretKeySelector()
        selector.name = secretName
        selector.key = secretKey
        source.secretKeyRef = selector
        envVar.valueFrom = source
        return envVar
    }

    /**
     * Creates a script for git setup with credentials.
     * 
     * @param repoUrl The URL of the git repository
     * @param mountPath The path where the repository volume is mounted
     * @return List of script lines
     */
    private fun createGitSetupScript(repoUrl: String, mountPath: String): List<String> {
        return listOf(
            "set -e",
            "echo 'Setting up git credentials'",
            "mkdir -p /git-credentials",
            "echo \"https://\$GIT_USERNAME:\$GIT_PASSWORD@github.com\" > /git-credentials/.git-credentials",
            "git config --global credential.helper 'store --file=/git-credentials/.git-credentials'",
            "echo 'Git credentials configured'",
            "git clone $repoUrl $mountPath",
            "echo 'Setting up git exclusions'",
            "echo '/.keruta' >> $mountPath/.git/info/exclude",
            "echo 'Git exclusions configured'"
        )
    }

    /**
     * Adds a volume mount to the main container.
     * 
     * @param container The container to add the volume mount to
     * @param volumeName The name of the volume to mount
     * @param mountPath The path where the volume should be mounted
     */
    private fun addVolumeToMainContainer(
        container: Container,
        volumeName: String,
        mountPath: String
    ) {
        val volumeMount = VolumeMount()
        volumeMount.name = volumeName
        volumeMount.mountPath = mountPath

        // Add volume mount to existing volume mounts or create new list
        if (container.volumeMounts == null) {
            container.volumeMounts = mutableListOf(volumeMount)
        } else {
            (container.volumeMounts as MutableList<VolumeMount>).add(volumeMount)
        }
    }
}
