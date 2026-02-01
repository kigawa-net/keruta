package net.kigawa.keruta.ktse.auth.oidc

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class OidcConf(
    @SerialName("jwks_uri")
    val jwksUri: String
)
