package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.usecase.server.websocket.WebsocketConnection
import net.kigawa.keruta.ktcp.usecase.server.websocket.WebsocketServer

class KtorWebsocketServer: WebsocketServer {
    override suspend fun bind(
        block: suspend (WebsocketConnection) -> Unit,
    ) {
        TODO("Not yet implemented")
    }
}
