package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpEntrypoints
import net.kigawa.keruta.ktcp.server.authenticate.ServerAuthenticateEntrypoint

class KtcpServer(

) {

    val ktcpEntrypoints = KtcpEntrypoints(
        ServerAuthenticateEntrypoint()
    )

    suspend fun startConnection(block: suspend (ServerConnection) -> Unit) {
        ServerConnection(this).also {
            block(it)
        }
    }
}
