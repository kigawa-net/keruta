package net.kigawa.keruta.ktcl.web.module

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.kodel.api.log.getKogger
import java.util.concurrent.ConcurrentHashMap

object WebsocketModule {
    private val logger = getKogger()
    private val connections = ConcurrentHashMap<String, WebSocketSession>()

    fun module(application: Application) = application.apply {
        install(WebSockets) {
            // WebSocket設定
        }

        routing {
            webSocket("/ws") {
                val sessionId = call.parameters["sessionId"] ?: "default"
                logger.info("WebSocket connection established for session: $sessionId")

                connections[sessionId] = this

                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            logger.info("Received message from $sessionId: $text")

                            // メッセージを処理
                            handleMessage(sessionId, text)
                        }
                    }
                } catch (e: Exception) {
                    logger.info("WebSocket error for session $sessionId: ${e.message}")
                } finally {
                    connections.remove(sessionId)
                    logger.info("WebSocket connection closed for session: $sessionId")
                }
            }
        }
    }

    private suspend fun handleMessage(sessionId: String, message: String) {
        // メッセージを処理するロジック
        // 例: メッセージを他の接続済みクライアントにブロードキャスト
        val response = "Echo from server: $message"

        connections[sessionId]?.send(Frame.Text(response))
    }

}
