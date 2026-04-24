package net.kigawa.keruta.kicp.domain.token

import kotlinx.serialization.Serializable

@Serializable
data class OidcToken(val value: String)