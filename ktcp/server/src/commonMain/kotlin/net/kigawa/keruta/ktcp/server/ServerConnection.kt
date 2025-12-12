package net.kigawa.keruta.ktcp.server

import kotlinx.coroutines.flow.MutableStateFlow

class Connection(
    val server: KtcpServer,
) {
    private val isAuthenticated = MutableStateFlow(false)
    fun authenticated() {
        isAuthenticated.value = true
    }
}
