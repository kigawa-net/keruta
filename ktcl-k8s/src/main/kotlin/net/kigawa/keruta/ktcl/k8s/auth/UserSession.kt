package net.kigawa.keruta.ktcl.k8s.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userSubject: String,
    val userIssuer: String,
    val userAudience: String,
    val token: String,
)
