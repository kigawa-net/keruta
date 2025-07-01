package net.kigawa.keruta.api.config

import net.kigawa.keruta.infra.security.jwt.JwtTokenProvider
import org.springframework.http.HttpHeaders
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

/**
 * Interceptor for WebSocket handshake requests that validates JWT tokens.
 */
@Component
class JwtWebSocketHandshakeInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        // Extract the token from the request headers or query parameters
        val token = extractToken(request)

        // If no token is provided, reject the handshake
        if (token == null) {
            return false
        }

        // Validate the token
        if (!jwtTokenProvider.validateToken(token)) {
            return false
        }

        // Get the authentication from the token and store it in the attributes
        val authentication = jwtTokenProvider.getAuthentication(token)
        attributes["authentication"] = authentication

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {
        // No action needed after handshake
    }

    /**
     * Extracts the JWT token from the request.
     *
     * @param request The HTTP request
     * @return The JWT token, or null if not found
     */
    private fun extractToken(request: ServerHttpRequest): String? {
        // Try to extract from Authorization header
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7)
        }

        // Try to extract from query parameters
        val uri = request.uri
        val query = uri.query
        if (query != null) {
            val params = query.split("&")
            for (param in params) {
                val parts = param.split("=")
                if (parts.size == 2 && parts[0] == "token") {
                    return parts[1]
                }
            }
        }

        return null
    }
}
