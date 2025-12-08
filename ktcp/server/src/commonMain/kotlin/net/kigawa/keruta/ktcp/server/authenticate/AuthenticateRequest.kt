package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg

data class AuthenticateRequest(
    val token: String,
    val clientType: String,
    val clientVersion: String,
    val capabilities: List<String>
) : AuthenticateMsg {
    override fun tryToAuthenticate(): AuthenticateRequest = this
}