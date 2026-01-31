package net.kigawa.keruta.ktcl.k8s.web.auth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class OidcDiscoveryResponse(
    @SerialName("issuer") val issuer: String,
    @SerialName("jwks_uri") val jwksUri: String,
    @SerialName("authorization_endpoint") val authorizationEndpoint: String? = null,
    @SerialName("token_endpoint") val tokenEndpoint: String? = null,
    @SerialName("userinfo_endpoint") val userinfoEndpoint: String? = null
)
