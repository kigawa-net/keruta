package net.kigawa.keruta.api.config

import net.kigawa.keruta.api.task.websocket.TaskLogWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val taskLogWebSocketHandler: TaskLogWebSocketHandler
) : WebSocketConfigurer {

    @Bean
    fun createWebSocketContainer(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        // Set larger buffer sizes to reduce CPU usage from frequent small messages
        container.maxTextMessageBufferSize = 8192
        container.maxBinaryMessageBufferSize = 8192
        // Set a reasonable timeout to prevent idle connections from consuming resources
        container.asyncSendTimeout = 30000
        // Set a maximum idle timeout to close inactive connections
        container.maxSessionIdleTimeout = 600000
        return container
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(taskLogWebSocketHandler, "/ws/tasks/{taskId}")
            .setAllowedOrigins("*")
    }
}
