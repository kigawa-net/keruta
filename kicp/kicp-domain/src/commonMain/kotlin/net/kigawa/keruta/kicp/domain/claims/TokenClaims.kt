package net.kigawa.keruta.kicp.domain.claims

import kotlinx.serialization.Serializable

@Serializable
data class TokenClaims(
    val issuer: String,
    val subject: String,
    val audience: List<String>,
)