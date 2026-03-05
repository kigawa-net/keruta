package net.kigawa.keruta.ktcp.usecase.server

import net.kigawa.keruta.ktcp.model.server.KtcpServer
import net.kigawa.keruta.ktcp.usecase.server.websocket.WebsocketServer

class KtcpServerBinder(
    private val webSocketServer: WebsocketServer,
) {
    suspend fun bind(ktcpServer: KtcpServer) {
        val ktcpMsgRouter = KtcpMsgRouter(ktcpServer.serverRoutes)
        webSocketServer.bind { connection ->
            connection.receive {
                ktcpMsgRouter.route(it)
            }
        }
    }
}
