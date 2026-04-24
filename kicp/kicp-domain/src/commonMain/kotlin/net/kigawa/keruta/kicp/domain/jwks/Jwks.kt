package net.kigawa.keruta.kicp.domain.jwks

import kotlinx.serialization.Serializable

@Serializable
data class Jwks(val keys: List<JwkKey>)