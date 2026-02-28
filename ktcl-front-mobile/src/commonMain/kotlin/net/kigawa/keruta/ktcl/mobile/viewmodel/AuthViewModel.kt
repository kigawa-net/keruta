package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.auth.TokenPair
import net.kigawa.keruta.ktcl.mobile.di.AppContainer
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import platform.Foundation.NSLog

data class AuthViewState(
    val isLoading: Boolean = false,
    val authState: AuthState = AuthState.Unauthenticated,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val authService: AuthService,
    private val appContainer: AppContainer? = null,
) : BaseViewModel<AuthViewState>(AuthViewState()) {

    init {
        viewModelScope.launch {
            authService.authState.collect { authState ->
                // 認証状態が変化したらisLoadingをfalseにする
                val newIsLoading = when (authState) {
                    is AuthState.Authenticated -> false
                    is AuthState.Error -> false
                    is AuthState.Unauthenticated -> false
                    is AuthState.Authenticating -> true
                }
                updateState { it.copy(authState = authState, isLoading = newIsLoading) }
            }
        }
    }

    fun startLogin() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        authService.setAuthenticating()
    }

    fun onLoginSuccess(tokens: TokenPair) {
        NSLog("=== AuthViewModel: onLoginSuccess called ===")
        updateState { it.copy(isLoading = false) }
        authService.setAuthenticated(tokens)

        // WebSocketに接続
        appContainer?.connectWebSocket(
            onConnected = {
                NSLog("=== AuthViewModel: WebSocket connected ===")
            },
            onError = { error ->
                NSLog("=== AuthViewModel: WebSocket error: ${error.message} ===")
                updateState { it.copy(errorMessage = "WebSocket接続に失敗しました: ${error.message}") }
            }
        )
    }

    fun onLoginError(message: String) {
        updateState { it.copy(isLoading = false, errorMessage = message) }
        authService.setError(message)
    }

    fun logout() {
        authService.logout()
        updateState { AuthViewState() }
    }

    fun clearError() {
        updateState { it.copy(errorMessage = null) }
    }
}
