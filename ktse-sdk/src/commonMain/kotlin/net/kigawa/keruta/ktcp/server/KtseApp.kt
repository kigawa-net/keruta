package net.kigawa.keruta.ktcp.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcp.domain.server.KtcpServer
import net.kigawa.keruta.ktcp.usecase.server.KtcpServerBinder
import kotlin.coroutines.CoroutineContext

class KtseApp(
    val coroutineContext: CoroutineContext,
) {
    val ktcpServerBinder: KtcpServerBinder? = null

    fun run(): Job {
        val ktcpServer = KtcpServer()
        return CoroutineScope(coroutineContext).launch {
            ktcpServerBinder?.bind(ktcpServer)
        }
    }
}
