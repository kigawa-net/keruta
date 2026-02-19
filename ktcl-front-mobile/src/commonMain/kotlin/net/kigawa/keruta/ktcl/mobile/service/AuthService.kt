package net.kigawa.keruta.ktcl.mobile.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.auth.TokenPair
import net.kigawa.keruta.ktcl.mobile.msg.auth.ServerAuthenticateMsg
import net.kigawa.keruta.ktcl.mobile.msg.auth.ServerAuthRequestMsg

class AuthService(
    private val messageSender: MessageSender,
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun setAuthenticating() {
        _authState.value = AuthState.Authenticating
    }

    fun setAuthenticated(tokens: TokenPair) {
        _authState.value = AuthState.Authenticated(tokens)
    }

    fun setError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun sendAuthentication(userToken: String, serverToken: String) {
        val connection = messageSender.connection.value ?: return
        val msg = ServerAuthRequestMsg(userToken = userToken, serverToken = serverToken)
        connection.send(json.encodeToString(msg))
    }
}
