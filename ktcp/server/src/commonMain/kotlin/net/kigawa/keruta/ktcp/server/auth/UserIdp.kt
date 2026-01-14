package net.kigawa.keruta.ktcp.server.auth

data class UserIdp(
    val audience: String,
    val subject: String,
    val issuer: String,
)
