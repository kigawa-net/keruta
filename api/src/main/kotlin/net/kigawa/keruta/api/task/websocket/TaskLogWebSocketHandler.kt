package net.kigawa.keruta.api.task.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import net.kigawa.keruta.api.task.dto.LogMessage
import net.kigawa.keruta.core.usecase.task.TaskService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class TaskLogWebSocketHandler(
    private val taskService: TaskService,
    private val objectMapper: ObjectMapper,
) : TextWebSocketHandler() {

    private val sessions = ConcurrentHashMap<String, MutableSet<WebSocketSession>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val taskId = extractTaskId(session)
        if (taskId != null) {
            sessions.computeIfAbsent(taskId) { ConcurrentHashMap.newKeySet() }.add(session)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val taskId = extractTaskId(session)
        if (taskId != null) {
            sessions[taskId]?.remove(session)
            if (sessions[taskId]?.isEmpty() == true) {
                sessions.remove(taskId)
            }
        }
    }

    fun sendLogUpdate(taskId: String, logContent: String, source: String = "stdout", level: String = "INFO") {
        val logMessage = LogMessage(
            taskId = taskId,
            source = source,
            level = level,
            message = logContent,
        )

        val messageJson = objectMapper.writeValueAsString(logMessage)
        val message = TextMessage(messageJson)

        sessions[taskId]?.forEach { session ->
            if (session.isOpen) {
                try {
                    session.sendMessage(message)
                } catch (e: Exception) {
                    // Handle exception
                }
            }
        }
    }

    private fun extractTaskId(session: WebSocketSession): String? {
        val path = session.uri?.path ?: return null
        val parts = path.split("/")
        return if (parts.size >= 4 && parts[1] == "ws" && parts[2] == "tasks") {
            parts[3]
        } else {
            null
        }
    }
}
