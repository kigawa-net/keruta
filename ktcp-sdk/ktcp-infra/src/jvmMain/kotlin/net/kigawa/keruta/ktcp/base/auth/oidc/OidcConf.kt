package net.kigawa.keruta.ktcp.base.auth.oidc

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class OidcConf(
    @SerialName("jwks_uri")
    val jwksUri: String,
    @SerialName("token_endpoint")
    val tokenEndpoint: String? = null,
)
