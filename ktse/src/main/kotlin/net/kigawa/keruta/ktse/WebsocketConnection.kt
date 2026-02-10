package net.kigawa.keruta.ktse

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.server.KtcpConnection
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class WebsocketConnection(
    val session: DefaultWebSocketServerSession,
): KtcpConnection {
    val logger = getKogger()
    override suspend fun send(msg: String) {
        logger.debug("send: $msg")
        session.send(msg)
    }
}
