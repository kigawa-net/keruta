package net.kigawa.keruta.infra.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Custom AuthenticationEntryPoint for REST API endpoints.
 * Returns 401 Unauthorized instead of redirecting to login page.
 * Formats error response according to API specifications.
 */
@Component
class RestAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        // Include the detailed cause of the error in the response
        val errorMessage = authException.message ?: "Unauthorized"

        // Format error response according to API specifications
        val errorResponse = mapOf(
            "error" to mapOf(
                "code" to "UNAUTHORIZED",
                "message" to errorMessage
            ),
            "meta" to mapOf(
                "timestamp" to ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "version" to "1.0.0"
            )
        )

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        objectMapper.writeValue(response.outputStream, errorResponse)
    }
}
