package net.kigawa.keruta.ktcl.k8s.web

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userId: String,
    val token: String,
)