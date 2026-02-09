package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.service.MessageSender

data class ProviderCreateViewState(
    val name: String = "",
    val issuer: String = "",
    val audience: String = "",
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val errorMessage: String? = null,
)

class ProviderCreateViewModel(
    private val messageSender: MessageSender,
) : BaseViewModel<ProviderCreateViewState>(ProviderCreateViewState()) {

    fun setName(name: String) {
        updateState { it.copy(name = name) }
    }

    fun setIssuer(issuer: String) {
        updateState { it.copy(issuer = issuer) }
    }

    fun setAudience(audience: String) {
        updateState { it.copy(audience = audience) }
    }

    fun createProvider() {
        val currentState = _state.value

        if (currentState.name.isBlank()) {
            updateState { it.copy(errorMessage = "プロバイダー名を入力してください") }
            return
        }

        if (currentState.issuer.isBlank()) {
            updateState { it.copy(errorMessage = "Issuerを入力してください") }
            return
        }

        if (currentState.audience.isBlank()) {
            updateState { it.copy(errorMessage = "Audienceを入力してください") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                messageSender.sendProviderCreate(
                    name = currentState.name,
                    issuer = currentState.issuer,
                    audience = currentState.audience,
                )
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
