/**
 * Represents the Kubernetes configuration settings in the system.
 *
 * @property id The unique identifier of the configuration
 * @property enabled Whether Kubernetes integration is enabled
 * @property configPath The path to the Kubernetes configuration file
 * @property inCluster Whether the application is running inside a Kubernetes cluster
 * @property defaultNamespace The default Kubernetes namespace
 * @property defaultImage The default Docker image for task execution
 * @property processorNamespace The namespace for the job processor
 * @property createdAt The timestamp when the configuration was created
 * @property updatedAt The timestamp when the configuration was last updated
 */
package net.kigawa.keruta.core.domain.model

import java.time.LocalDateTime

data class KubernetesConfig(
    val id: String? = null,
    val enabled: Boolean = false,
    val configPath: String = "",
    val inCluster: Boolean = false,
    val defaultNamespace: String = "default",
    val defaultImage: String = "keruta-task-executor:latest",
    val processorNamespace: String = "default",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)