package net.kigawa.keruta.infra.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

/**
 * Filter for logging HTTP request and response details.
 * This provides detailed information about the requests and responses,
 * which is useful for debugging and monitoring.
 */
@Component
@Order(-90) // Run after LoggingFilter (-100) but before most other filters
class RequestLoggingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Wrap request and response to cache their content
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        // Log request headers
        val requestHeaders = buildRequestHeadersLog(wrappedRequest)
        logger.debug("Request Headers: $requestHeaders")

        val startTime = System.currentTimeMillis()

        try {
            // Continue with the filter chain
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            // Log request body
            val requestBody = getRequestBody(wrappedRequest)
            if (requestBody.isNotBlank()) {
                logger.debug("Request Body: $requestBody")
            }

            // Log response status and headers
            val responseStatus = wrappedResponse.status
            val responseHeaders = buildResponseHeadersLog(wrappedResponse)
            logger.debug("Response Status: $responseStatus, Headers: $responseHeaders, Duration: ${duration}ms")

            // Log response body for non-binary content
            val contentType = wrappedResponse.contentType
            if (contentType != null && (contentType.contains("json") || contentType.contains("text"))) {
                val responseBody = getResponseBody(wrappedResponse)
                if (responseBody.isNotBlank()) {
                    logger.debug("Response Body: $responseBody")
                }
            }

            // Copy content to the original response
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun buildRequestHeadersLog(request: HttpServletRequest): String {
        val headers = StringBuilder()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            // Skip sensitive headers
            if (headerName.equals("Authorization", ignoreCase = true) ||
                headerName.equals("Cookie", ignoreCase = true)
            ) {
                headers.append("$headerName: [REDACTED], ")
            } else {
                headers.append("$headerName: ${request.getHeader(headerName)}, ")
            }
        }
        return headers.toString()
    }

    private fun buildResponseHeadersLog(response: HttpServletResponse): String {
        val headers = StringBuilder()
        for (headerName in response.headerNames) {
            // Skip sensitive headers
            if (headerName.equals("Set-Cookie", ignoreCase = true)) {
                headers.append("$headerName: [REDACTED], ")
            } else {
                headers.append("$headerName: ${response.getHeader(headerName)}, ")
            }
        }
        return headers.toString()
    }

    private fun getRequestBody(request: ContentCachingRequestWrapper): String {
        val content = request.contentAsByteArray
        return if (content.isNotEmpty()) {
            String(content, StandardCharsets.UTF_8)
        } else {
            ""
        }
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String {
        val content = response.contentAsByteArray
        return if (content.isNotEmpty()) {
            String(content, StandardCharsets.UTF_8)
        } else {
            ""
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // Skip logging for static resources
        val path = request.requestURI
        return path.contains("/static/") ||
               path.contains("/css/") ||
               path.contains("/js/") ||
               path.contains("/images/") ||
               path.contains("/favicon.ico")
    }
}
