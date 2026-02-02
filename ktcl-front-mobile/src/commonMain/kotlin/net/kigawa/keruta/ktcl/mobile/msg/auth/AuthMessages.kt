package net.kigawa.keruta.ktcl.mobile.msg.auth

import kotlinx.serialization.Serializable

@Serializable
data class ServerAuthRequestMsg(
    val type: String = "auth_request",
    val userToken: String,
    val serverToken: String,
)

@Serializable
data class ClientAuthSuccessMsg(
    val type: String = "auth_success",
)
