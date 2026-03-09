package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import platform.Foundation.NSLog

actual class MobileWebSocketConnection(
    private val session: DefaultClientWebSocketSession,
) {
    private val _messages = MutableSharedFlow<String>()
    actual val messages: SharedFlow<String> = _messages.asSharedFlow()

    actual suspend fun send(msg: String) {
        NSLog("=== MobileWebSocketConnection: sending: $msg ===")
        session.send(Frame.Text(msg))
        NSLog("=== MobileWebSocketConnection: send completed ===")
    }

    suspend fun onMessageReceived(text: String) {
        NSLog("=== MobileWebSocketConnection: onMessageReceived: $text ===")
        _messages.emit(text)
    }

    actual suspend fun close() {
        session.close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
    }
}
