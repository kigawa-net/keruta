package net.kigawa.keruta.ktcl.k8s.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class WellKnownKerutaResponse(
    val service: String,
    val version: String,
    val auth: AuthInfo
)

@Serializable
data class AuthInfo(
    val issuer: String,
    val loginUrl: String,
)