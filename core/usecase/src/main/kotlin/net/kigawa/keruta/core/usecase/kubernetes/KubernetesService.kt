/**
 * Service interface for Kubernetes operations.
 */
package net.kigawa.keruta.core.usecase.kubernetes

import net.kigawa.keruta.core.domain.model.KubernetesConfig
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task

interface KubernetesService {
    /**
     * Creates a Kubernetes Job with task information as environment variables.
     * The Job will create a Pod to execute the task.
     *
     * @param task The task to create a job for
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace
     * @param jobName The name of the job
     * @param resources The resource requirements
     * @param additionalEnv Additional environment variables
     * @return The name of the created job
     */
    fun createJob(
        task: Task,
        image: String,
        namespace: String,
        jobName: String?,
        resources: Resources?,
        additionalEnv: Map<String, String>
    ): String

    /**
     * Gets the logs of a job's pod.
     *
     * @param namespace The namespace of the job
     * @param jobName The name of the job
     * @return The logs of the job's pod
     */
    fun getJobLogs(namespace: String, jobName: String): String

    /**
     * Deletes a job.
     *
     * @param namespace The namespace of the job
     * @param jobName The name of the job
     * @return true if the job was deleted, false otherwise
     */
    fun deleteJob(namespace: String, jobName: String): Boolean

    /**
     * Gets the status of a job.
     *
     * @param namespace The namespace of the job
     * @param jobName The name of the job
     * @return The status of the job
     */
    fun getJobStatus(namespace: String, jobName: String): String

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

    /**
     * Creates a Kubernetes pod with task information as environment variables.
     * @deprecated Use createJob instead to align with documentation
     *
     * @param task The task to create a pod for
     * @param image The Docker image to use
     * @param namespace The Kubernetes namespace
     * @param podName The name of the pod
     * @param resources The resource requirements
     * @param additionalEnv Additional environment variables
     * @return The name of the created pod
     */
    @Deprecated("Use createJob instead to align with documentation", ReplaceWith("createJob(task, image, namespace, podName, resources, additionalEnv)"))
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
     * @deprecated Use getJobLogs instead to align with documentation
     *
     * @param namespace The namespace of the pod
     * @param podName The name of the pod
     * @return The logs of the pod
     */
    @Deprecated("Use getJobLogs instead to align with documentation", ReplaceWith("getJobLogs(namespace, podName)"))
    fun getPodLogs(namespace: String, podName: String): String

    /**
     * Deletes a pod.
     * @deprecated Use deleteJob instead to align with documentation
     *
     * @param namespace The namespace of the pod
     * @param podName The name of the pod
     * @return true if the pod was deleted, false otherwise
     */
    @Deprecated("Use deleteJob instead to align with documentation", ReplaceWith("deleteJob(namespace, podName)"))
    fun deletePod(namespace: String, podName: String): Boolean

    /**
     * Gets the status of a pod.
     * @deprecated Use getJobStatus instead to align with documentation
     *
     * @param namespace The namespace of the pod
     * @param podName The name of the pod
     * @return The status of the pod
     */
    @Deprecated("Use getJobStatus instead to align with documentation", ReplaceWith("getJobStatus(namespace, podName)"))
    fun getPodStatus(namespace: String, podName: String): String
}
