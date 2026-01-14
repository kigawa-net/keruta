package net.kigawa.keruta.ktcp.server.auth

data class ProviderConfig(
    val issuer: String,
    val audience: String,
)
