package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig
import platform.Foundation.NSLog

actual class ConnectionManager actual constructor(
    private val config: MobileConfig,
) {
    private val client = HttpClient(Darwin) {
        install(WebSockets)
    }

    private var webSocketConnection: MobileWebSocketConnection? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    actual suspend fun connect(): MobileKtcpConnection {
        NSLog("=== ConnectionManager: connecting to ${config.websocketUrl} ===")
        val session = client.webSocketSession(config.websocketUrl)
        NSLog("=== ConnectionManager: session created ===")

        webSocketConnection = MobileWebSocketConnection(session)

        NSLog("=== ConnectionManager: starting message receiver in scope ===")
        scope.launch {
            NSLog("=== ConnectionManager: receiver coroutine started ===")
            try {
                NSLog("=== ConnectionManager: waiting for incoming frames ===")
                while (true) {
                    NSLog("=== ConnectionManager: about to receive ===")
                    val frame = session.incoming.receive()
                    NSLog("=== ConnectionManager: received frame: $frame ===")
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            NSLog("=== ConnectionManager: received text: $text ===")
                            webSocketConnection?.onMessageReceived(text)
                        }
                        is Frame.Close -> {
                            NSLog("=== ConnectionManager: connection closed by server ===")
                            break
                        }
                        else -> {
                            NSLog("=== ConnectionManager: received other frame: ${frame::class.simpleName} ===")
                        }
                    }
                }
            } catch (e: Exception) {
                NSLog("=== ConnectionManager: receiver error: ${e.message} ===")
                e.printStackTrace()
            }
        }

        NSLog("=== ConnectionManager: connect returning ===")
        return MobileKtcpConnection(webSocketConnection!!)
    }

    actual fun disconnect() {
        webSocketConnection?.let { conn ->
            CoroutineScope(Dispatchers.Default).launch {
                conn.close()
            }
        }
        webSocketConnection = null
    }
}
