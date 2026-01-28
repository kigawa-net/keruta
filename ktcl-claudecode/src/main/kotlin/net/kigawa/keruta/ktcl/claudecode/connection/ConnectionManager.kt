package net.kigawa.keruta.ktcl.claudecode.connection

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import net.kigawa.keruta.ktcl.claudecode.config.ClaudeCodeConfig

class ConnectionManager(
    private val config: ClaudeCodeConfig,
) {
    private val client = HttpClient {
        install(WebSockets)
    }

    suspend fun connect(): JvmWebSocketConnection {
        val session = client.webSocketSession(
            method = HttpMethod.Get,
            host = config.ktseHost,
            port = config.ktsePort,
            path = "/ws/ktcp"
        )
        return JvmWebSocketConnection(session)
    }
}