package net.kigawa.keruta.ktcl.mobile.provider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.kigawa.keruta.ktcl.mobile.msg.provider.Provider

class ProviderRepository {
    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers.asStateFlow()

    fun updateProviders(newProviders: List<Provider>) {
        _providers.value = newProviders
    }

    fun addProvider(provider: Provider) {
        _providers.value = _providers.value + provider
    }
}
