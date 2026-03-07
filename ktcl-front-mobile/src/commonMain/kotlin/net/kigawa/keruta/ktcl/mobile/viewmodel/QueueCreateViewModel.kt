package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.msg.provider.Provider
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import net.kigawa.keruta.ktcl.mobile.service.MessageSender

data class QueueCreateViewState(
    val name: String = "",
    val selectedProviderId: Long? = null,
    val providers: List<Provider> = emptyList(),
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val errorMessage: String? = null,
)

class QueueCreateViewModel(
    private val providerRepository: ProviderRepository,
    private val messageSender: MessageSender,
    private val authService: AuthService,
): BaseViewModel<QueueCreateViewState>(QueueCreateViewState()) {

    init {
        viewModelScope.launch {
            providerRepository.providers.collect { providers ->
                println("=== QueueCreateViewModel: providers updated, count=${providers.size} ===")
                updateState { it.copy(providers = providers, isLoading = false) }
            }
        }
    }

    fun loadProviders() {
        println("=== QueueCreateViewModel: loadProviders called ===")
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // 接続が確立されるまで待つ
                println("=== QueueCreateViewModel: waiting for connection ===")
                val connection = messageSender.connection.first { it != null }
                if (connection == null) {
                    updateState { it.copy(isLoading = false, errorMessage = "WebSocket接続に失敗しました") }
                    return@launch
                }
                println("=== QueueCreateViewModel: connection established ===")

                // 認証が完了するまで待つ
                println("=== QueueCreateViewModel: waiting for auth ===")
                val authState = authService.authState.first { it is AuthState.Authenticated }
                if (authState !is AuthState.Authenticated) {
                    updateState { it.copy(isLoading = false, errorMessage = "認証に失敗しました") }
                    return@launch
                }
                println("=== QueueCreateViewModel: authenticated ===")

                println("=== QueueCreateViewModel: sending provider list request ===")
                messageSender.sendProviderList()
            } catch (e: Exception) {
                println("=== QueueCreateViewModel: error: ${e.message} ===")
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun setName(name: String) {
        updateState { it.copy(name = name) }
    }

    fun setSelectedProvider(providerId: Long) {
        updateState { it.copy(selectedProviderId = providerId) }
    }

    fun createQueue() {
        val currentState = _state.value
        val providerId = currentState.selectedProviderId
        val name = currentState.name

        if (providerId == null) {
            updateState { it.copy(errorMessage = "プロバイダーを選択してください") }
            return
        }

        if (name.isBlank()) {
            updateState { it.copy(errorMessage = "キュー名を入力してください") }
            return
        }

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

                messageSender.sendQueueCreate(providerId, name, "")
                updateState { it.copy(isLoading = false, isCreated = true) }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun resetCreatedState() {
        updateState { it.copy(isCreated = false) }
    }
}
