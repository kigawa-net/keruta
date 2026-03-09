package net.kigawa.keruta.ktse.auth.keruta

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class TokenExchangeResponse(
    @SerialName("id_token")
    val idToken: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
)
