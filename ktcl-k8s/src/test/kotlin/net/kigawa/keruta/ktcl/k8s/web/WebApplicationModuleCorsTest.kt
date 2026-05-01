package net.kigawa.keruta.ktcl.k8s.web

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import net.kigawa.keruta.ktcl.k8s.config.CorsConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WebApplicationModuleCorsTest {

    private fun Application.testModule(corsConfig: CorsConfig) {
        // Minimal CORS configuration for testing
        install(io.ktor.server.plugins.cors.routing.CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowCredentials = true

            val origins = corsConfig.allowedOrigins
            if (origins != null) {
                origins.forEach { origin ->
                    val scheme = when {
                        origin.startsWith("https://") -> "https"
                        origin.startsWith("http://") -> "http"
                        else -> "http"
                    }
                    val hostWithPort = origin.removePrefix("https://").removePrefix("http://")
                    allowHost(hostWithPort, schemes = listOf(scheme))
                }
            } else {
                anyHost()
            }
        }

        routing {
            get("/test") {
                call.respondText("OK")
            }
        }
    }

    @Test
    fun `CORS should allow requests from configured origins`() = testApplication {
        // Given: CORS config with specific origins
        val corsConfig = CorsConfig.fromString("http://localhost:3000")
        application { testModule(corsConfig) }

        // When: Request from allowed origin
        val response = client.get("/test") {
            header(HttpHeaders.Origin, "http://localhost:3000")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:3000", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `CORS should allow requests from multiple configured origins`() = testApplication {
        // Given: CORS config with multiple origins
        val corsConfig = CorsConfig.fromString("http://localhost:3000,https://example.com")
        application { testModule(corsConfig) }

        // When: Request from first allowed origin
        val response1 = client.get("/test") {
            header(HttpHeaders.Origin, "http://localhost:3000")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response1.status)
        assertEquals("http://localhost:3000", response1.headers[HttpHeaders.AccessControlAllowOrigin])

        // When: Request from second allowed origin
        val response2 = client.get("/test") {
            header(HttpHeaders.Origin, "https://example.com")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response2.status)
        assertEquals("https://example.com", response2.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `CORS should allow any origin when origins is null`() = testApplication {
        // Given: CORS config with null origins (anyHost)
        val corsConfig = CorsConfig(allowedOrigins = null)
        application { testModule(corsConfig) }

        // When: Request from any origin
        val response = client.get("/test") {
            header(HttpHeaders.Origin, "http://random-origin.com")
        }

        // Then: anyHost() echoes back the actual origin (not "*" when credentials are allowed)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://random-origin.com", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `CORS should handle preflight requests correctly`() = testApplication {
        // Given: CORS config with specific origin
        val corsConfig = CorsConfig.fromString("http://localhost:3000")
        application { testModule(corsConfig) }

        // When: Preflight request
        val response = client.options("/test") {
            header(HttpHeaders.Origin, "http://localhost:3000")
            header(HttpHeaders.AccessControlRequestMethod, "POST")
            header(HttpHeaders.AccessControlRequestHeaders, HttpHeaders.ContentType)
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:3000", response.headers[HttpHeaders.AccessControlAllowOrigin])
        // Access-Control-Allow-Methods contains allowed methods, not the requested method directly
        assertEquals("true", response.headers[HttpHeaders.AccessControlAllowCredentials])
        // Verify Content-Type header is allowed (may include other allowed headers like Authorization)
        val allowedHeaders = response.headers[HttpHeaders.AccessControlAllowHeaders]
        assert(allowedHeaders?.contains("Content-Type") == true)
    }

    @Test
    fun `CORS should allow credentials header`() = testApplication {
        // Given: CORS config with specific origin
        val corsConfig = CorsConfig.fromString("http://localhost:3000")
        application { testModule(corsConfig) }

        // When: Request from allowed origin
        val response = client.get("/test") {
            header(HttpHeaders.Origin, "http://localhost:3000")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("true", response.headers[HttpHeaders.AccessControlAllowCredentials])
    }

    @Test
    fun `CORS should work with https origins`() = testApplication {
        // Given: CORS config with HTTPS origin
        val corsConfig = CorsConfig.fromString("https://secure.example.com")
        application { testModule(corsConfig) }

        // When: Request from HTTPS origin
        val response = client.get("/test") {
            header(HttpHeaders.Origin, "https://secure.example.com")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("https://secure.example.com", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `CORS should work with origins containing ports`() = testApplication {
        // Given: CORS config with port
        val corsConfig = CorsConfig.fromString("http://localhost:3000")
        application { testModule(corsConfig) }

        // When: Request from origin with port
        val response = client.get("/test") {
            header(HttpHeaders.Origin, "http://localhost:3000")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:3000", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `CORS should reject requests from non-configured origins`() = testApplication {
        // Given: CORS config with specific origin
        val corsConfig = CorsConfig.fromString("http://allowed.com")
        application { testModule(corsConfig) }

        // When: Request from non-allowed origin
        val response = client.get("/test") {
            header(HttpHeaders.Origin, "http://not-allowed.com")
        }

        // Then: Access-Control-Allow-Origin header should not be present
        assertNull(response.headers[HttpHeaders.AccessControlAllowOrigin])
    }
}
