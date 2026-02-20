package net.kigawa.keruta.ktcl.k8s.auth

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.kigawa.kodel.api.log.getKogger
import java.io.OutputStream
import java.net.HttpURLConnection
import java.nio.charset.StandardCharsets

/**
 * OIDCトークン取得サービス
 *
 * 優先順位:
 * 1. Service Account Token (K8s Volume Projection)
 * 2. Keycloak Service Account (Client Credentials Flow)
 */
class OidcTokenProvider(
    private val config: OidcConfig,
) {
    private val logger = getKogger()

    data class OidcConfig(
        val issuer: String,
        val clientId: String,
        val clientSecret: String,
        val serviceAccountTokenPath: String,
    )

    /**
     * トークンを取得
     * Pair<userToken, serverToken>を返す
     */
    fun getTokens(): Pair<String, String> {
        // 1. Service Account Tokenを試す
        val serviceAccountToken = tryGetServiceAccountToken()
        if (serviceAccountToken != null) {
            logger.info { "Using Service Account Token from volume projection" }
            return Pair(serviceAccountToken, serviceAccountToken)
        }

        // 2. Keycloak Service Accountを試す
        val keycloakTokens = tryGetKeycloakTokens()
        if (keycloakTokens != null) {
            logger.info { "Using Keycloak Service Account tokens" }
            return keycloakTokens
        }

        throw IllegalStateException("Failed to get OIDC tokens from any source")
    }

    /**
     * K8s Service Account Tokenを取得
     * Token Projectionが有効な場合、/var/run/secrets/tokens/にトークンがマウントされる
     */
    private fun tryGetServiceAccountToken(): String? {
        return try {
            val tokenFile = java.io.File(config.serviceAccountTokenPath)
            if (tokenFile.exists()) {
                tokenFile.readText().trim()
            } else {
                logger.info { "Service account token file not found: ${config.serviceAccountTokenPath}" }
                null
            }
        } catch (e: Exception) {
            logger.info { "Failed to read service account token: ${e.message}" }
            null
        }
    }

    /**
     * Keycloak Service Accountからトークンを取得 (Client Credentials Flow)
     */
    private fun tryGetKeycloakTokens(): Pair<String, String>? {
        return try {
            runBlocking {
                // OIDC DiscoveryでToken Endpointを取得
                val discoveryUrl = "${config.issuer}/.well-known/openid-configuration"
                val discoveryJson = httpGet(discoveryUrl)
                val discovery = Json.decodeFromString<OidcDiscovery>(discoveryJson)

                val tokenEndpoint = discovery.token_endpoint
                    ?: throw IllegalStateException("Token endpoint not found in OIDC discovery")

                // Client Credentials Flowでトークンを取得
                val formData = "grant_type=client_credentials" +
                    "&client_id=${config.clientId}" +
                    "&client_secret=${config.clientSecret}" +
                    "&scope=openid profile email"

                val responseJson = httpPost(tokenEndpoint, formData)
                val response = Json.decodeFromString<TokenResponse>(responseJson)

                val accessToken = response.access_token
                    ?: throw IllegalStateException("Access token not found in response")

                Pair(accessToken, accessToken)
            }
        } catch (e: Exception) {
            logger.info { "Failed to get Keycloak tokens: ${e.message}" }
            null
        }
    }

    @Suppress("DEPRECATION")
    private fun httpGet(urlString: String): String {
        val url = java.net.URI.create(urlString).toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    @Suppress("DEPRECATION")
    private fun httpPost(urlString: String, body: String): String {
        val url = java.net.URI.create(urlString).toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val outputStream: OutputStream = connection.outputStream
        outputStream.write(body.toByteArray(StandardCharsets.UTF_8))
        outputStream.flush()
        outputStream.close()

        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    @kotlinx.serialization.Serializable
    private data class OidcDiscovery(
        val token_endpoint: String?,
    )

    @kotlinx.serialization.Serializable
    private data class TokenResponse(
        val access_token: String?,
        val refresh_token: String? = null,
        val expires_in: Long? = null,
        val token_type: String? = null,
    )

    companion object {
        fun fromEnvironment(): OidcConfig {
            return OidcConfig(
                issuer = System.getenv("IDP_ISSUER")
                    ?: throw IllegalStateException("IDP_ISSUER is required"),
                clientId = System.getenv("KTSE_OIDC_CLIENT_ID")
                    ?: System.getenv("IDP_CLIENT_ID")
                    ?: "keruta",
                clientSecret = System.getenv("KTSE_OIDC_CLIENT_SECRET")
                    ?: System.getenv("IDP_CLIENT_SECRET")
                    ?: "",
                serviceAccountTokenPath = System.getenv("KTSE_OIDC_TOKEN_PATH")
                    ?: "/var/run/secrets/tokens/keruta-token",
            )
        }
    }
}
