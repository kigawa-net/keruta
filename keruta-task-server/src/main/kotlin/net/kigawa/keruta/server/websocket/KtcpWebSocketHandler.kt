package net.kigawa.keruta.server.websocket

import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcp.model.AuthenticateMessage
import net.kigawa.keruta.ktcp.model.ErrorMessage
import net.kigawa.keruta.ktcp.model.HeartbeatMessage
import net.kigawa.keruta.ktcp.model.KtcpMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class KtcpWebSocketHandler(
    private val ktcpMessageService: KtcpMessageService
) : TextWebSocketHandler() {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val logger = LoggerFactory.getLogger(KtcpWebSocketHandler::class.java)

    private val sessions = mutableMapOf<String, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("WebSocket connection established: ${session.id}")
        sessions[session.id] = session
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("WebSocket connection closed: ${session.id}, status: $status")
        sessions.remove(session.id)
        ktcpMessageService.handleDisconnect(session.id)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val ktcpMessage = json.decodeFromString<KtcpMessage>(message.payload)
            logger.debug("Received message type: ${ktcpMessage.type} from session: ${session.id}")

            when (ktcpMessage) {
                is AuthenticateMessage -> handleAuthentication(session, ktcpMessage)
                is HeartbeatMessage -> handleHeartbeat(session, ktcpMessage)
                else -> ktcpMessageService.handleMessage(session.id, ktcpMessage)
            }
        } catch (e: Exception) {
            logger.error("Error handling message from session ${session.id}", e)
            sendError(session, "INVALID_MESSAGE", "Failed to parse message: ${e.message}", false)
        }
    }

    private fun handleAuthentication(session: WebSocketSession, message: AuthenticateMessage) {
        try {
            val authenticated = ktcpMessageService.authenticate(session.id, message)
            if (authenticated) {
                logger.info("Authentication successful for session: ${session.id}")
                // Send success response if needed
            } else {
                logger.warn("Authentication failed for session: ${session.id}")
                sendError(session, "AUTH_FAILED", "Authentication failed", false)
                session.close(CloseStatus(1008, "Authentication failed"))
            }
        } catch (e: Exception) {
            logger.error("Authentication error for session ${session.id}", e)
            sendError(session, "AUTH_FAILED", "Authentication error: ${e.message}", false)
            session.close(CloseStatus(1008, "Authentication failed"))
        }
    }

    private fun handleHeartbeat(session: WebSocketSession, message: HeartbeatMessage) {
        // Update last heartbeat time
        ktcpMessageService.updateHeartbeat(session.id)
        logger.debug("Heartbeat received from session: ${session.id}")
    }

    private fun sendError(session: WebSocketSession, code: String, message: String, retryable: Boolean) {
        try {
            val errorMessage = ErrorMessage(code, message, retryable)
            val jsonString = json.encodeToString(errorMessage)
            session.sendMessage(TextMessage(jsonString))
        } catch (e: Exception) {
            logger.error("Failed to send error message to session ${session.id}", e)
        }
    }

    fun sendMessage(sessionId: String, message: KtcpMessage) {
        val session = sessions[sessionId]
        if (session != null && session.isOpen) {
            try {
                val jsonString = json.encodeToString(message)
                session.sendMessage(TextMessage(jsonString))
                logger.debug("Sent message type: ${message.type} to session: $sessionId")
            } catch (e: Exception) {
                logger.error("Failed to send message to session $sessionId", e)
            }
        } else {
            logger.warn("Session $sessionId not found or closed")
        }
    }
}