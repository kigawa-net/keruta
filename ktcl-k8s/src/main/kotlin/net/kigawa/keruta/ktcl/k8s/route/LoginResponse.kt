package net.kigawa.keruta.ktcl.k8s.route

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val userId: String? = null,
    val message: String? = null,
)
