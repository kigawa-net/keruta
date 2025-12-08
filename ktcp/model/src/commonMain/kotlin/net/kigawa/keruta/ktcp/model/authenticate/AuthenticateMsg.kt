package net.kigawa.keruta.ktcp.model.authenticate

/**
 * Marker interface for authentication messages.
 */
interface AuthenticateMsg {
    val token: UnverifiedAuthenticateToken

}
