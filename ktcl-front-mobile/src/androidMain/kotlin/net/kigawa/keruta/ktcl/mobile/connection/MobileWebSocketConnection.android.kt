package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText

actual class MobileWebSocketConnection(
    private val session: DefaultClientWebSocketSession,
) {
    actual suspend fun send(msg: String) {
        session.send(Frame.Text(msg))
    }

    actual suspend fun receive(): String? {
        return when (val frame = session.incoming.receive()) {
            is Frame.Text -> frame.readText()
            else -> null
        }
    }

    actual suspend fun close() {
        session.close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
    }
}
