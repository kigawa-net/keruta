package net.kigawa.keruta.ktcl.mobile.connection

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig

actual class ConnectionManager actual constructor(
    private val config: MobileConfig,
) {
    private val client = HttpClient(Darwin) {
        install(WebSockets)
    }

    actual suspend fun connect(): MobileKtcpConnection {
        var connection: MobileWebSocketConnection? = null

        client.webSocket(config.websocketUrl) {
            connection = MobileWebSocketConnection(this)
        }

        return MobileKtcpConnection(
            connection ?: throw IllegalStateException("WebSocket接続に失敗しました"),
        )
    }
}
