package net.kigawa.keruta.kise.websocket

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.kise.KiseConfig
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.log.traceignore.warn
import kotlin.time.Duration.Companion.seconds

/**
 * Kise WebSocketサーバーモジュール
 *
 * 認証用のWebSocketエンドポイントを提供
 */
class KiseWebSocketServer(
    private val application: Application,
    private val config: KiseConfig,
) {
    private val logger = getKogger()

    init {
        application.install(WebSockets.Plugin) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }

    fun websocketModule(routing: Route) = routing.webSocket("/ws/kise") {
        logger.warn("Kise WebSocket connection established")
        handleConnection(this)
    }

    private suspend fun DefaultWebSocketServerSession.handleConnection(
        session: WebSocketServerSession,
    ) {
        logger.warn("Kise session started")
        incoming.consumeEach { frame ->
            handleFrame(frame, session)
        }
    }

    private suspend fun handleFrame(frame: Frame, session: WebSocketServerSession) {
        when (frame) {
            is Frame.Text -> {
                val text = frame.readText()
                logger.debug("Received text frame: $text")
                processMessage(text, session)
            }
            is Frame.Binary -> {
                logger.debug("Received binary frame")
            }
            else -> {
                logger.debug("Received other frame type: ${frame.frameType}")
            }
        }
    }

    private suspend fun processMessage(text: String, session: WebSocketServerSession) {
        // TODO: メッセージの処理
        // - AuthRequestMsg の受信
        // - 認証ユースケースの実行
        // - AuthResponseMsg の返信
        logger.warn("Message processing not implemented yet")
        session.send(Frame.Text("{\"error\": \"not implemented\"}"))
    }
}
