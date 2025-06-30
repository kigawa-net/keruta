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
import java.util.concurrent.Executor

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val taskLogWebSocketHandler: TaskLogWebSocketHandler
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

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(taskLogWebSocketHandler, "/ws/tasks/{taskId}")
            .setAllowedOrigins("*")
            .addInterceptors(tokenAuthenticationInterceptor())
    }

    /**
     * Creates a HandshakeInterceptor that checks for a token parameter in the URL.
     * This implements a simple token-based authentication for WebSocket connections.
     */
    private fun tokenAuthenticationInterceptor(): HandshakeInterceptor {
        return object : HandshakeInterceptor {
            override fun beforeHandshake(
                request: ServerHttpRequest,
                response: ServerHttpResponse,
                wsHandler: WebSocketHandler,
                attributes: MutableMap<String, Any>
            ): Boolean {
                // Extract the token from the query parameters
                val uri = request.uri
                val query = uri.query

                if (query != null) {
                    val params = query.split("&")
                    for (param in params) {
                        val parts = param.split("=")
                        if (parts.size == 2 && parts[0] == "token") {
                            val token = parts[1]
                            // Store the token in the attributes for later use
                            attributes["token"] = token
                            return true
                        }
                    }
                }

                // If no token is provided, reject the handshake
                return false
            }

            override fun afterHandshake(
                request: ServerHttpRequest,
                response: ServerHttpResponse,
                wsHandler: WebSocketHandler,
                exception: Exception?
            ) {
                // No action needed after handshake
            }
        }
    }
}
