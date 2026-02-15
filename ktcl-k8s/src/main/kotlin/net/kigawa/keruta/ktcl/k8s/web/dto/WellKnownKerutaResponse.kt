package net.kigawa.keruta.ktcl.k8s.web.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WellKnownKerutaResponse(
    val service: String,
    val version: String,
    val issuer: String,
    @SerialName("authorization_endpoint")
    val authorizationEndpoint: String,
    val audience: String,
)
