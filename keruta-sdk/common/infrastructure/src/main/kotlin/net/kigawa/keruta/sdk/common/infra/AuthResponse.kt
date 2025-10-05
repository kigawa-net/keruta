package net.kigawa.keruta.sdk.common.infra

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val id: String,
    val isSuccess: Boolean,
)
