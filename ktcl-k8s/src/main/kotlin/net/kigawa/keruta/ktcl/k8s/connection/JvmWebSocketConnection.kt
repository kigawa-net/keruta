package net.kigawa.keruta.ktcl.k8s.connection

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import net.kigawa.keruta.ktcp.model.KtcpConnection

class JvmWebSocketConnection(
    private val session: DefaultClientWebSocketSession,
) : KtcpConnection {
    override suspend fun send(msg: String) {
        session.send(Frame.Text(msg))
    }

    suspend fun receive(): String? {
        return when (val frame = session.incoming.receive()) {
            is Frame.Text -> frame.readText()
            else -> null
        }
    }
}
