package net.kigawa.keruta.ktcl.k8s.auth

import kotlinx.serialization.Serializable

typealias CodeVerifier = String
typealias CodeChallenge = String
typealias State = String
typealias Nonce = String

@Serializable
class Pkce(
    val codeVerifier: CodeVerifier,
    val codeChallenge: CodeChallenge, val state: State,
    val nonce: Nonce,
)
