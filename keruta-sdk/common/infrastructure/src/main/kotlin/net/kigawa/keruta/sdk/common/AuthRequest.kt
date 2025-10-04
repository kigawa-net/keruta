package net.kigawa.keruta.sdk.common

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val id: String,
) {
}
