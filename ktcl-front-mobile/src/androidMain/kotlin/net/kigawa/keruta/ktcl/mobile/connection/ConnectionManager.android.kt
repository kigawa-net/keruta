package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

actual class ConnectionManager actual constructor(
    private val config: MobileConfig,
) {
    private val client = HttpClient(OkHttp) {
        install(WebSockets)
    }

    private var webSocketConnection: MobileWebSocketConnection? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    actual suspend fun connect(): MobileKtcpConnection {
        val session = client.webSocketSession(config.websocketUrl)

        webSocketConnection = MobileWebSocketConnection(session)

        // メッセージ受信用の口を立ち上げる
        scope.launch {
            try {
                println("=== ConnectionManager: starting message receiver ===")
                while (true) {
                    val frame = session.incoming.receive()
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("=== ConnectionManager: received: $text ===")
                            webSocketConnection?.onMessageReceived(text)
                        }
                        is Frame.Close -> {
                            println("=== ConnectionManager: connection closed ===")
                            break
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                println("=== ConnectionManager: error: ${e.message} ===")
            }
        }

        return MobileKtcpConnection(webSocketConnection!!)
    }

    fun disconnect() {
        scope.cancel()
        webSocketConnection?.let { conn ->
            CoroutineScope(Dispatchers.IO).launch {
                conn.close()
            }
        }
        webSocketConnection = null
    }
}
