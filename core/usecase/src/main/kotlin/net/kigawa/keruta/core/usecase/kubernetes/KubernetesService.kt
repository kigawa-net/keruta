/**
 * Service interface for Kubernetes operations.
 */
package net.kigawa.keruta.core.usecase.kubernetes

import net.kigawa.keruta.core.domain.model.KubernetesConfig
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task

interface KubernetesService {
    /**
     * Creates a Kubernetes pod with task information as environment variables.
     *
     * @param task The task to create a pod for
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace
     * @param podName The name of the pod
     * @param resources The resource requirements
     * @param additionalEnv Additional environment variables
     * @return The name of the created pod
     */
    fun createPod(
        task: Task,
        image: String,
        namespace: String,
        podName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): String

    /**
     * Gets the logs of a pod.
     *
     * @param namespace The namespace of the pod
     * @param podName The name of the pod
     * @return The logs of the pod
     */
    fun getPodLogs(namespace: String, podName: String): String

    /**
     * Deletes a pod.
     *
     * @param namespace The namespace of the pod
     * @param podName The name of the pod
     * @return true if the pod was deleted, false otherwise
     */
    fun deletePod(namespace: String, podName: String): Boolean

    /**
     * Gets the status of a pod.
     *
     * @param namespace The namespace of the pod
     * @param podName The name of the pod
     * @return The status of the pod
     */
    fun getPodStatus(namespace: String, podName: String): String

    /**
     * Gets the current Kubernetes configuration.
     *
     * @return The current Kubernetes configuration
     */
    fun getConfig(): KubernetesConfig

    /**
     * Updates the Kubernetes configuration.
     *
     * @param config The new Kubernetes configuration
     * @return The updated Kubernetes configuration
     */
    fun updateConfig(config: KubernetesConfig): KubernetesConfig
}
