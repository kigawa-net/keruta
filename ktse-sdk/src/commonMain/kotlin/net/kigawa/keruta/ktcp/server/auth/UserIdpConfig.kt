package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.kodel.api.net.Url

data class UserIdpConfig(
    val issuer: Url,
    val audience: String,
)
