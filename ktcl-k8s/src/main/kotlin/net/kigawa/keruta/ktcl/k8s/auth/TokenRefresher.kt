package net.kigawa.keruta.ktcl.k8s.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import net.kigawa.keruta.ktcl.k8s.config.IdpConfig
import net.kigawa.keruta.ktcl.k8s.login.TokenResponse
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug

class TokenRefresher(
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher,
    private val idpConfig: IdpConfig,
) {
    private val logger = getKogger()

    suspend fun refresh(refreshToken: String): TokenResponse {
        logger.debug { "Token refresh requested" }
        val discovery = oidcDiscoveryFetcher.fetchByIssuer(idpConfig.issuer)
        val tokenEndpoint = discovery.tokenEndpoint
            ?: throw IllegalStateException("Token endpoint not found in OIDC discovery")

        logger.info("Refreshing access token via $tokenEndpoint")

        val client = HttpClient(CIO) {
            install(ContentNegotiation) { json() }
        }

        return client.use { httpClient ->
            val response = httpClient.submitForm(
                url = tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "refresh_token")
                    append("refresh_token", refreshToken)
                    append("client_id", idpConfig.clientId)
                }
            )
            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                throw TokenRefreshException("Token refresh failed (${response.status.value}): $errorBody")
            }
            response.body<TokenResponse>()
        }
    }
}
