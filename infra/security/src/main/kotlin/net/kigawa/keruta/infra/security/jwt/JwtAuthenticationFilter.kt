/**
 * Filter for JWT authentication.
 * This filter extracts the JWT token from the request, validates it,
 * and sets the authentication in the SecurityContextHolder.
 */
package net.kigawa.keruta.infra.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)

        if (token != null) {
            logger.debug("JWT token found in request")

            if (jwtTokenProvider.validateToken(token)) {
                logger.debug("JWT token validated successfully")

                try {
                    val authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                    logger.debug("Authentication set in SecurityContextHolder: user=${authentication.name}, authorities=${authentication.authorities}")
                } catch (e: Exception) {
                    logger.error("Failed to set authentication from JWT token", e)
                }
            } else {
                logger.warn("Invalid JWT token: ${maskToken(token)}")
            }
        } else {
            logger.debug("No JWT token found in request")
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Masks a token for logging purposes to avoid exposing sensitive information.
     *
     * @param token The token to mask
     * @return The masked token
     */
    private fun maskToken(token: String): String {
        return if (token.length > 10) {
            "${token.substring(0, 5)}...${token.substring(token.length - 5)}"
        } else {
            "***"
        }
    }

    /**
     * Extracts the JWT token from the request.
     *
     * @param request The HTTP request
     * @return The JWT token, or null if not found
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
