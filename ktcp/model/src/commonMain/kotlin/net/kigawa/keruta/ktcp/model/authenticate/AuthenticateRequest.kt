package net.kigawa.keruta.ktcp.model.authenticate

data class AuthenticateRequest(
    val token: String,
    val clientType: String,
    val clientVersion: String,
    val capabilities: List<String>
) : AuthenticateMsg