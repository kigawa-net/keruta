package net.kigawa.keruta.ktcl.mobile.msg.provider

import kotlinx.serialization.Serializable

@Serializable
data class ServerProviderListMsg(
    val type: String = "provider_list",
)

@Serializable
data class Provider(
    val id: String,
    val name: String,
    val issuer: String,
    val audience: String,
)

@Serializable
data class ClientProviderListMsg(
    val type: String = "provider_listed",
    val providers: List<Provider>,
)

