package net.kigawa.keruta.ktse

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.server.KtcpConnection

class WebsocketConnection(
    val session: DefaultWebSocketServerSession,
): KtcpConnection {
    override suspend fun send(msg: String) {
        session.send(msg)
    }
}
