package net.kigawa.keruta.server.service

import net.kigawa.keruta.ktcp.model.AuthenticateMessage
import net.kigawa.keruta.ktcp.model.KtcpMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
open class KtcpMessageService {

    private val logger = LoggerFactory.getLogger(KtcpMessageService::class.java)

    private val authenticatedSessions = mutableSetOf<String>()

    open fun authenticate(sessionId: String, message: AuthenticateMessage): Boolean {
        // TODO: Implement JWT token validation
        // For now, accept all authentications
        logger.info("Authenticating session $sessionId with token ${message.token.take(10)}...")
        authenticatedSessions.add(sessionId)
        return true
    }

    open fun handleMessage(sessionId: String, message: KtcpMessage) {
        if (sessionId !in authenticatedSessions) {
            logger.warn("Unauthenticated session $sessionId tried to send message type: ${message.type}")
            return
        }

        logger.info("Handling message type: ${message.type} from session: $sessionId")

        when (message) {
            // TODO: Implement message handling for each type
            else -> logger.warn("Unhandled message type: ${message.type}")
        }
    }

    open fun handleDisconnect(sessionId: String) {
        authenticatedSessions.remove(sessionId)
        logger.info("Session $sessionId disconnected")
    }

    open fun updateHeartbeat(sessionId: String) {
        // TODO: Update last heartbeat timestamp
        logger.debug("Heartbeat updated for session: $sessionId")
    }
}