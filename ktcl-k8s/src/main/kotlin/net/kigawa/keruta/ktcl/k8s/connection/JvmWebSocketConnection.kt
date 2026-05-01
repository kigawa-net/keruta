package net.kigawa.keruta.ktcl.k8s.connection

import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.client.KtcpConnection

class JvmWebSocketConnection(
    private val session: DefaultClientWebSocketSession,
): KtcpConnection {
    override suspend fun send(msg: String) {
        session.send(Frame.Text(msg))
    }

    suspend fun receive(): String? {
        return when (val frame = session.incoming.receive()) {
            is Frame.Text -> frame.readText()
            else -> null
        }
    }

    suspend fun close() {
        session.close()
    }
}
