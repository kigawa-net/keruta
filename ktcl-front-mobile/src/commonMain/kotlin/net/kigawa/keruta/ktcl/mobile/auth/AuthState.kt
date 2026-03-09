package net.kigawa.keruta.ktcl.mobile.auth

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticating : AuthState()
    data class Authenticated(val tokens: TokenPair) : AuthState()
    data class Error(val message: String) : AuthState()
}
