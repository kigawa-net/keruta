package net.kigawa.keruta.ktcl.k8s.web.login

import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.k8s.auth.Pkce

@Serializable
data class OidcSession(
    val redirectUri: String,
    val issuer: String,
    val clientId: String,
    val pkce: Pkce,
)
