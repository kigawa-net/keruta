package net.kigawa.keruta.ktcl.k8s.web.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URI

class OidcDiscoveryFetcher {
    private val logger = LoggerFactory.get("OidcDiscoveryFetcher")
    suspend fun fetchByIssuer(issuer: URI): OidcDiscoveryResponse {
        val strUrl = issuer.toString().removeSuffix("/") + "/.well-known/openid-configuration"
        return fetch(strUrl)
    }

    suspend fun fetch(wellKnownUrl: String): OidcDiscoveryResponse {
        val client = createHttpClient()

        return try {
            val response = client.get(wellKnownUrl)
            val rawBody = response.body<String>()
            logger.info("OIDC discovery response from $wellKnownUrl: $rawBody")
            
            // レスポンスが空でないかチェック
            if (rawBody.isBlank()) {
                throw RuntimeException("OIDC discovery response is empty from $wellKnownUrl")
            }
            
            kotlinx.serialization.json.Json.decodeFromString<OidcDiscoveryResponse>(rawBody)
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
