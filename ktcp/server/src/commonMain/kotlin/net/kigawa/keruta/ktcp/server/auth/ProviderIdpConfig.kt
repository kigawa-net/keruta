package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.kodel.api.net.Url

data class ProviderIdpConfig(
    val issuer: Url,
    val audience: String,
    val name: String,
)
