package net.kigawa.keruta.ktcp.usecase.server

import net.kigawa.keruta.ktcp.model.server.KtcpServer
import net.kigawa.keruta.ktcp.usecase.server.websocket.WebsocketServer

class KtcpServerBinder(
    private val webSocketServer: WebsocketServer,
) {
    suspend fun bind(ktcpServer: KtcpServer) {
        val ktcpMsgRouter = KtcpMsgRouter(ktcpServer)
        webSocketServer.bind {
            it.receive {
//                ktcpMsgRouter.route(it)
            }
        }
    }
}
