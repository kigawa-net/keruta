package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.msg.provider.Provider
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.service.MessageSender

data class ProviderListViewState(
    val providers: List<Provider> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ProviderListViewModel(
    private val providerRepository: ProviderRepository,
    private val messageSender: MessageSender,
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
                messageSender.sendProviderList()
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
