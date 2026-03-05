package net.kigawa.keruta.ktcp.usecase.server.websocket

import net.kigawa.keruta.ktcp.model.WebsocketPacket

interface WebsocketConnection {
    suspend fun receive(block: (WebsocketPacket) -> Unit)
}
