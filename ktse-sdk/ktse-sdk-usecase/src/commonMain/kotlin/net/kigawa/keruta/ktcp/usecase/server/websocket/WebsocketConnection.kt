package net.kigawa.keruta.ktcp.usecase.server.websocket

import net.kigawa.keruta.ktcp.domain.WebsocketPacket

interface WebsocketConnection {
    suspend fun receive(block: (WebsocketPacket) -> Unit)
}
