package net.kigawa.keruta.api.config

import net.kigawa.keruta.api.task.websocket.TaskLogWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val taskLogWebSocketHandler: TaskLogWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(taskLogWebSocketHandler, "/ws/tasks/{taskId}")
            .setAllowedOrigins("*")
    }
}
