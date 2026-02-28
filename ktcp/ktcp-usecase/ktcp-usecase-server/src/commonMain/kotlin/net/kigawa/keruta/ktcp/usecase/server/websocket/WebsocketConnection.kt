package net.kigawa.keruta.ktcp.usecase.server.websocket

interface WebsocketConnection {
    suspend fun receive(block: (WebsocketPacket) -> Unit)
}
