package net.kigawa.keruta.ktcl.k8s.web.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import net.kigawa.kodel.api.log.LoggerFactory

class OidcDiscoveryFetcher {
    private val logger = LoggerFactory.get("OidcDiscoveryFetcher")

    suspend fun fetch(wellKnownUrl: String): OidcDiscoveryResponse {
        val client = createHttpClient()

        return try {
            client.get(wellKnownUrl).body<OidcDiscoveryResponse>()
        } catch (e: Exception) {
            logger.severe("Failed to fetch OIDC metadata from $wellKnownUrl: ${e.message}")
            throw RuntimeException("Failed to fetch OIDC metadata", e)
        } finally {
            client.close()
        }
    }

    private fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}
