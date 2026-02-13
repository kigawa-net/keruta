package net.kigawa.keruta.ktcp.server.persist

data class ProviderIdpInput(
    val issuer: String,
    val subject: String,
    val audience: String,
)