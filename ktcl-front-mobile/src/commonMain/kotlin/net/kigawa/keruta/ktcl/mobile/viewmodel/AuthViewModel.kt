package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.auth.TokenPair
import net.kigawa.keruta.ktcl.mobile.service.AuthService

data class AuthViewState(
    val isLoading: Boolean = false,
    val authState: AuthState = AuthState.Unauthenticated,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val authService: AuthService,
) : BaseViewModel<AuthViewState>(AuthViewState()) {

    init {
        viewModelScope.launch {
            authService.authState.collect { authState ->
                updateState { it.copy(authState = authState) }
            }
        }
    }

    fun startLogin() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        authService.setAuthenticating()
    }

    fun onLoginSuccess(tokens: TokenPair) {
        updateState { it.copy(isLoading = false) }
        authService.setAuthenticated(tokens)
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
