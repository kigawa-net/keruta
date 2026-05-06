package net.kigawa.keruta.ktse.kicp

import kotlinx.serialization.Serializable

/**
 * kicp登録リクエストのボディ
 */
@Serializable
data class RegisterRequest(
    val oidcToken: String,
    val providerToken: String,
    val registerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)
