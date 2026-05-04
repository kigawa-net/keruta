package net.kigawa.keruta.kise.route

import io.ktor.http.*
import io.ktor.http.URLBuilder
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.kise.KiseConfig
import net.kigawa.keruta.kise.oidc.OidcDiscoveryFetcher
import net.kigawa.keruta.kise.oidc.PkceGenerator
import net.kigawa.keruta.kise.oidc.model.OidcDiscoveryResponse
import net.kigawa.keruta.kise.oidc.model.OidcSession
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URI

class LoginRoute(
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher,
    private val pkceGenerator: PkceGenerator,
    private val config: KiseConfig,
) {
    private val logger = LoggerFactory.get("LoginRoute")

    fun configure(route: Route) = route.get("/login") {
        val issuer = call.queryParameters["issuer"]?.let { URI(it) } ?: URI(config.defaultUserIdpIssuer)
        val clientId = call.queryParameters["clientId"] ?: config.oidcClientId
        val redirectUri = call.queryParameters["redirect_uri"] ?: config.oidcRedirectUri

        logger.info("Starting OIDC login flow for issuer: $issuer, clientId: $clientId")

        try {
            val discoveryResponse = oidcDiscoveryFetcher.fetchByIssuer(issuer)
            val pkce = pkceGenerator.generate()

            // セッションにOIDC情報を保存
            val oidcSession = OidcSession(
                pkce = pkce,
                redirectUri = redirectUri,
                issuer = issuer.toString(),
                clientId = clientId,
            )
            call.sessions.set(oidcSession)

            // 認可エンドポイントにリダイレクト
            respondRedirectIssuer(discoveryResponse, clientId, redirectUri, pkce)
        } catch (e: Exception) {
            logger.severe("Failed to start OIDC login flow: ${e.message}")
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Failed to start login flow", "message" to e.message)
            )
        }
    }

    private suspend fun RoutingContext.respondRedirectIssuer(
        discoveryResponse: net.kigawa.keruta.kise.oidc.model.OidcDiscoveryResponse,
        clientId: String,
        redirectUri: String,
        pkce: net.kigawa.keruta.kise.oidc.model.Pkce,
    ) {
        val authUrl = URLBuilder(discoveryResponse.authorizationEndpoint).apply {
            parameters.append("client_id", clientId)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("response_type", "code")
            parameters.append("scope", "openid profile email offline_access")
            parameters.append("state", pkce.state)
            parameters.append("nonce", pkce.nonce)
            parameters.append("code_challenge", pkce.codeChallenge)
            parameters.append("code_challenge_method", "S256")
        }.buildString()

        logger.info("Redirecting to authorization endpoint: $authUrl")
        call.respondRedirect(authUrl)
    }
}
