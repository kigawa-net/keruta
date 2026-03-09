package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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

    actual fun disconnect() {
        scope.cancel()
        webSocketConnection?.let { conn ->
            CoroutineScope(Dispatchers.IO).launch {
                conn.close()
            }
        }
        webSocketConnection = null
    }
}
