package net.kigawa.keruta.sdk.common.infra

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val id: String,
    val token: String
) {
}
