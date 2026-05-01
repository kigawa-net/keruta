package net.kigawa.keruta.ktcl.k8s.route

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val token: String,
)
