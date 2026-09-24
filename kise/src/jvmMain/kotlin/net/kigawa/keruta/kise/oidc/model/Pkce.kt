package net.kigawa.keruta.kise.oidc.model

import kotlinx.serialization.Serializable

typealias CodeVerifier = String
typealias CodeChallenge = String
typealias State = String
typealias Nonce = String

@Serializable
data class Pkce(
    val codeVerifier: CodeVerifier,
    val codeChallenge: CodeChallenge,
    val state: State,
    val nonce: Nonce,
)
