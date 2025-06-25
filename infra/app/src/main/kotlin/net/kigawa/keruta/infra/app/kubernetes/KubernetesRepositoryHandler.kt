package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.Volume
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Git repository setup in Kubernetes jobs.
 * Coordinates the setup of volumes, init containers, and volume mounts for Git repositories.
 */
@Component
class KubernetesRepositoryHandler(
    private val repositoryVolumeHandler: KubernetesRepositoryVolumeHandler,
    private val gitContainerHandler: KubernetesGitContainerHandler,
    private val gitCredentialsHandler: KubernetesGitCredentialsHandler
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
        val repoVolume = repositoryVolumeHandler.createRepositoryVolume(task, repository, namespace, repoVolumeName)
            ?: return false
        volumes.add(repoVolume)

        // Create git clone container
        val gitCloneContainer = gitContainerHandler.createGitCloneContainer(repository, repoVolumeName, repoMountPath)

        // Setup git environment variables and handle credentials
        gitCredentialsHandler.setupGitCredentials(
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
        gitContainerHandler.addVolumeToMainContainer(mainContainer, repoVolumeName, repoMountPath)

        return true
    }
}