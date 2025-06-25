package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.Volume
import io.fabric8.kubernetes.api.model.VolumeMount
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes volumes.
 * Responsible for creating and configuring volumes for Kubernetes jobs.
 */
@Component
class KubernetesVolumeHandler {
    private val logger = LoggerFactory.getLogger(KubernetesVolumeHandler::class.java)

    /**
     * Creates a work volume and adds it to the volumes list.
     * Also adds a volume mount to the main container.
     *
     * @param volumes The list of volumes to add to
     * @param mainContainer The main container to add the volume mount to
     * @return The name of the created volume
     */
    fun createWorkVolume(
        volumes: MutableList<Volume>,
        mainContainer: Container
    ): String {
        logger.info("Creating work volume")
        
        // Create a new work volume
        val workVolume = Volume()
        workVolume.name = "work-volume"
        workVolume.emptyDir = io.fabric8.kubernetes.api.model.EmptyDirVolumeSource()
        volumes.add(workVolume)

        // Add volume mount to main container
        val workVolumeMount = VolumeMount()
        workVolumeMount.name = "work-volume"
        workVolumeMount.mountPath = "/work"

        // Add volume mount to existing volume mounts or create new list
        if (mainContainer.volumeMounts == null) {
            mainContainer.volumeMounts = mutableListOf(workVolumeMount)
        } else {
            (mainContainer.volumeMounts as MutableList<VolumeMount>).add(workVolumeMount)
        }

        return "work-volume"
    }
}