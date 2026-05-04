package net.kigawa.keruta.kise.oidc.model

import kotlinx.serialization.Serializable

/**
 * OIDCセッション情報
 * Ktorのセッション機能で使用
 */
@Serializable
data class OidcSession(
    val pkce: Pkce,
    val redirectUri: String,
    val issuer: String,
    val clientId: String,
    val registerToken: String? = null,
)
