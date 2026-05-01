package net.kigawa.keruta.ktcp.usecase.server.websocket

interface WebsocketServer {
    suspend fun bind(block: suspend (WebsocketConnection) -> Unit)
}
