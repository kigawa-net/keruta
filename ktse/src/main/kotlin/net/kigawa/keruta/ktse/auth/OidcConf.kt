package net.kigawa.keruta.ktse.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class OidcConf(
    @SerialName("jwks_url")
    val jwksUrl: String
)
