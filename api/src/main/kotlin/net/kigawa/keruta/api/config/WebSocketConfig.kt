package net.kigawa.keruta.api.config

import net.kigawa.keruta.api.task.websocket.TaskLogWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean
import jakarta.websocket.server.ServerContainer
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.util.concurrent.Executor

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val taskLogWebSocketHandler: TaskLogWebSocketHandler,
    private val jwtWebSocketHandshakeInterceptor: JwtWebSocketHandshakeInterceptor,
) : WebSocketConfigurer {

    /**
     * Creates a dedicated thread pool for WebSocket operations.
     * This helps prevent WebSocket connections from hogging CPU resources.
     */
    @Bean(name = ["webSocketTaskExecutor"])
    fun webSocketTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        // Set core pool size (number of threads to keep in the pool, even if they are idle)
        executor.corePoolSize = 10
        // Set max pool size (maximum number of threads to allow in the pool)
        executor.maxPoolSize = 30
        // Set queue capacity (size of the queue used for holding tasks before they are executed)
        executor.queueCapacity = 50
        // Set thread name prefix for better identification in logs and monitoring
        executor.setThreadNamePrefix("WebSocket-")
        // Initialize the executor
        executor.initialize()
        return executor
    }

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

    @Bean
    fun handshakeHandler(): DefaultHandshakeHandler {
        return DefaultHandshakeHandler()
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(taskLogWebSocketHandler, "/ws/tasks/{taskId}")
            .setAllowedOrigins("*")
            .addInterceptors(jwtWebSocketHandshakeInterceptor)
            .setHandshakeHandler(handshakeHandler())
    }

}
