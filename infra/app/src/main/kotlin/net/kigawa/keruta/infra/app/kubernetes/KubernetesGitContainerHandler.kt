package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.VolumeMount
import net.kigawa.keruta.core.domain.model.Repository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Git container operations in Kubernetes.
 * Responsible for creating and configuring Git containers for repository operations.
 */
@Component
class KubernetesGitContainerHandler {
    private val logger = LoggerFactory.getLogger(KubernetesGitContainerHandler::class.java)

    /**
     * Creates a container for git clone operations.
     * 
     * @param repository The Git repository
     * @param volumeName The name of the volume to mount
     * @param mountPath The path where the volume should be mounted
     * @return The created container
     */
    fun createGitCloneContainer(
        repository: Repository,
        volumeName: String,
        mountPath: String
    ): Container {
        val gitCloneContainer = Container()
        gitCloneContainer.name = "git-clone"
        gitCloneContainer.image = "alpine/git:latest"

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
     * Adds a volume mount to the main container.
     * 
     * @param container The container to add the volume mount to
     * @param volumeName The name of the volume to mount
     * @param mountPath The path where the volume should be mounted
     */
    fun addVolumeToMainContainer(
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
