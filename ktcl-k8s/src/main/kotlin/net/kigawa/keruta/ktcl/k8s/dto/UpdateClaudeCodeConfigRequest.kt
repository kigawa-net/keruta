package net.kigawa.keruta.ktcl.k8s.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateClaudeCodeConfigRequest(
    val anthropicApiKey: String,
)
