package net.kigawa.keruta.server.config

import net.kigawa.keruta.server.websocket.KtcpWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val ktcpWebSocketHandler: KtcpWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(ktcpWebSocketHandler, "/ws/ktcp")
            .setAllowedOrigins("*") // Configure appropriately for production
    }
}