package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder
import io.fabric8.kubernetes.api.model.Volume
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Handler for repository volume operations in Kubernetes.
 * Responsible for creating volumes and persistent volume claims for Git repositories.
 */
@Component
class KubernetesRepositoryVolumeHandler(
    private val clientProvider: KubernetesClientProvider
) {
    private val logger = LoggerFactory.getLogger(KubernetesRepositoryVolumeHandler::class.java)

    /**
     * Creates a volume for the repository.
     * 
     * @param task The task associated with the job
     * @param repository The Git repository
     * @param namespace The Kubernetes namespace
     * @param volumeName The name to use for the volume
     * @return The created volume, or null if creation failed
     */
    fun createRepositoryVolume(
        task: Task,
        repository: Repository,
        namespace: String,
        volumeName: String
    ): Volume? {
        val repoVolume = Volume()
        repoVolume.name = volumeName

        logger.info("Using PVC for repository: ${repository.name}")

        // Determine PVC name based on parent task
        val pvcName = if (task.parentId != null) {
            // Use parent task's PVC if available
            "git-repo-pvc-${task.parentId}"
        } else {
            // Create new PVC for this task
            "git-repo-pvc-${task.id}"
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
}