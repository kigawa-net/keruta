package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual class MobileWebSocketConnection(
    private val session: DefaultClientWebSocketSession,
) {
    private val _messages = MutableSharedFlow<String>()
    actual val messages: SharedFlow<String> = _messages.asSharedFlow()

    actual suspend fun send(msg: String) {
        session.send(Frame.Text(msg))
    }

    suspend fun onMessageReceived(text: String) {
        _messages.emit(text)
    }

    actual suspend fun close() {
        session.close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
    }
}
