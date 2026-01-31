package net.kigawa.keruta.ktcl.k8s.web.routes

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val token: String,
)
