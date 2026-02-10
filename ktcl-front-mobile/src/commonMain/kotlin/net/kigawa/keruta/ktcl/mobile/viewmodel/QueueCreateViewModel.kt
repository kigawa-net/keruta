package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.msg.provider.Provider
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
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
) : BaseViewModel<QueueCreateViewState>(QueueCreateViewState()) {

    init {
        viewModelScope.launch {
            providerRepository.providers.collect { providers ->
                updateState { it.copy(providers = providers) }
            }
        }
    }

    fun loadProviders() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                messageSender.sendProviderList()
            } catch (e: Exception) {
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
                messageSender.sendQueueCreate(providerId, name)
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
