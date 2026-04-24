package net.kigawa.keruta.kicp.domain.jwks

import kotlinx.serialization.Serializable

@Serializable
data class JwkKey(
    val kty: String,
    val use: String? = null,
    val kid: String? = null,
    val alg: String? = null,
    val n: String? = null,
    val e: String? = null,
    val x: String? = null,
    val y: String? = null,
    val crv: String? = null,
)