package net.kigawa.keruta.ktcp.server.auth

data class Idp(
    val audience: String,
    val subject: String,
    val issuer: String,
)
