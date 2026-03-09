package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.msg.provider.Provider
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import net.kigawa.keruta.ktcl.mobile.service.MessageSender

data class ProviderListViewState(
    val providers: List<Provider> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ProviderListViewModel(
    private val providerRepository: ProviderRepository,
    private val messageSender: MessageSender,
    private val authService: AuthService,
) : BaseViewModel<ProviderListViewState>(ProviderListViewState()) {

    init {
        viewModelScope.launch {
            providerRepository.providers.collect { providers ->
                updateState { it.copy(providers = providers, isLoading = false) }
            }
        }
    }

    fun loadProviders() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 接続が確立されるまで待つ
                val connection = messageSender.connection.first { it != null }
                if (connection == null) {
                    updateState { it.copy(isLoading = false, errorMessage = "WebSocket接続に失敗しました") }
                    return@launch
                }

                // 認証が完了するまで待つ
                val authState = authService.authState.first { it is AuthState.Authenticated }
                if (authState !is AuthState.Authenticated) {
                    updateState { it.copy(isLoading = false, errorMessage = "認証に失敗しました") }
                    return@launch
                }

                messageSender.sendProviderList()
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
