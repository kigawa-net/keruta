package net.kigawa.keruta.ktcl.k8s.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse(
    val kubernetes: KubernetesConfig,
    val queue: QueueConfig
)
