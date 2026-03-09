package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
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

    fun logout() {
        authService.logout()
        updateState { AuthViewState() }
    }

}
