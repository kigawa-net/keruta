package net.kigawa.keruta.ktcp.model.authenticate

import net.kigawa.keruta.ktcp.model.err.ValidateErr
import net.kigawa.kodel.api.err.Res

/**
 * Marker interface for authentication messages.
 */
interface AuthenticateMsg {
    val token: AuthenticateToken

    fun tryToAuthenticate(): Res<AuthenticateMsg, ValidateErr>?
}
