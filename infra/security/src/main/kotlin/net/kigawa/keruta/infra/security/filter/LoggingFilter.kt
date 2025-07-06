package net.kigawa.keruta.infra.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * Filter for adding request and user information to the MDC (Mapped Diagnostic Context).
 * This enhances logs with contextual information like request IDs and user IDs.
 */
@Component
@Order(-100) // Ensure this filter runs early in the chain
class LoggingFilter : OncePerRequestFilter(), Ordered {

    override fun getOrder(): Int {
        return -100 // Same order as the @Order annotation
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = request.getHeader("X-Request-ID") ?: UUID.randomUUID().toString()

        try {
            // Add request ID to MDC
            MDC.put("requestId", requestId)

            // Add response header for request tracking
            response.addHeader("X-Request-ID", requestId)

            // Add user ID to MDC if authenticated
            SecurityContextHolder.getContext().authentication?.let { auth ->
                if (auth.isAuthenticated && auth.name != "anonymousUser") {
                    MDC.put("userId", auth.name)
                } else {
                    MDC.put("userId", "anonymous")
                }
            } ?: MDC.put("userId", "anonymous")

            // Add request information to MDC
            MDC.put("method", request.method)
            MDC.put("path", request.requestURI)
            MDC.put("ip", request.remoteAddr)

            // Log the request
            logger.debug("Request: ${request.method} ${request.requestURI} from IP: ${request.remoteAddr}")

            // Continue with the filter chain
            filterChain.doFilter(request, response)
        } finally {
            // Always clear the MDC after the request is processed
            MDC.clear()
        }
    }
}
