package net.kigawa.keruta.ktse.kicp

import kotlinx.serialization.Serializable

@Serializable
data class GetRegisterTokenRequest(
    val oidcToken: String,
    val providerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)
