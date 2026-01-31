package net.kigawa.keruta.ktcl.k8s.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse(
    val kubernetes: KubernetesConfig,
    val queue: QueueConfig
)

@Serializable
data class KubernetesConfig(
    val namespace: String,
    val useInCluster: Boolean,
    val kubeconfigPath: String?,
    val jobTimeout: Long
)

@Serializable
data class QueueConfig(
    val queueId: Long
)

@Serializable
data class UpdateKubernetesConfigRequest(
    val namespace: String? = null,
    val useInCluster: Boolean? = null,
    val kubeconfigPath: String? = null,
    val jobTimeout: Long? = null
)

@Serializable
data class UpdateQueueConfigRequest(
    val queueId: Long
)