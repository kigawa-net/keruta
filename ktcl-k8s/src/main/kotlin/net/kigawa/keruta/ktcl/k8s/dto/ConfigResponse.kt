package net.kigawa.keruta.ktcl.k8s.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse(
    val kubernetes: KubernetesConfig,
    val queue: QueueConfig,
    val claudeCode: ClaudeCodeConfig,
    val hasGithubToken: Boolean,
)
