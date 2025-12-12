package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.KtcpEntrypoints
import net.kigawa.keruta.ktcp.server.authenticate.ServerAuthenticateEntrypoint

class KtcpServer(

) {

    val ktcpEntrypoints = KtcpEntrypoints(
        ServerAuthenticateEntrypoint()
    )

    fun startConnection(): ServerConnection {
        return ServerConnection(this)
    }
}
