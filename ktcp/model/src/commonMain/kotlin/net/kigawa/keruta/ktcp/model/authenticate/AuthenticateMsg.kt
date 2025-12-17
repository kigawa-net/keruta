package net.kigawa.keruta.ktcp.model.authenticate

import kotlinx.serialization.Serializable

/**
 * Marker interface for authentication messages.
 */
@Serializable
data class AuthenticateMsg(
    val token: AuthenticateToken
) {
}
