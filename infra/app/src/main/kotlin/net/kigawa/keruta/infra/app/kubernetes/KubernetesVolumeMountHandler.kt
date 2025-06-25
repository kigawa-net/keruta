package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.VolumeMount
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes volume mounts.
 * Responsible for setting up volume mounts for containers.
 */
@Component
class KubernetesVolumeMountHandler {
    private val logger = LoggerFactory.getLogger(KubernetesVolumeMountHandler::class.java)

    /**
     * Sets up volume mount for the container.
     *
     * @param container The container to set up volume mounts for
     * @param volumeName The name of the volume
     * @param mountPath The mount path of the volume
     */
    fun setupVolumeMount(container: Container, volumeName: String, mountPath: String) {
        // Check if a volume mount with the same path already exists
        val volumeMounts = container.volumeMounts ?: mutableListOf()
        val existingMount = volumeMounts.find { it.mountPath == mountPath }

        if (existingMount != null) {
            logger.info("Volume mount with path $mountPath already exists, skipping")
            return
        }

        // Create volume mount for work directory
        val volumeMount = VolumeMount()
        volumeMount.name = volumeName
        volumeMount.mountPath = mountPath

        // Add volume mount to container
        volumeMounts.add(volumeMount)
        container.volumeMounts = volumeMounts
    }
}