package net.kigawa.keruta.ktcl.k8s.web.login

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.KerutaEndpoints
import net.kigawa.keruta.ktcl.k8s.auth.Pkce
import net.kigawa.keruta.ktcl.k8s.auth.PkceGenerator
import net.kigawa.keruta.ktcl.k8s.config.IdpConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.web.auth.OidcDiscoveryResponse
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.net.Url
import java.net.URI

class LoginRoute(
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher,
    private val pkceGenerator: PkceGenerator,
    val idpConfig: IdpConfig,
    val kerutaEndpoints: KerutaEndpoints,
) {
    private val logger = LoggerFactory.get("LoginRoute")

    fun configure(route: Route) = route.get("/login") {
        val issuer = call.queryParameters["issuer"]?.let { URI(it) } ?: idpConfig.issuer
        val clientId = call.queryParameters["clientId"] ?: idpConfig.clientId

        logger.info("Starting OIDC login flow for issuer: $issuer, clientId: $clientId")

        try {
            val discoveryResponse = oidcDiscoveryFetcher.fetchByIssuer(issuer)
            val pkce = pkceGenerator.generate()
            val redirectUri = kerutaEndpoints.callback
            saveSession(pkce, redirectUri, issuer, clientId)
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

    fun RoutingContext.saveSession(pkce: Pkce, redirectUri: Url, issuer: URI, clientId: String) {
        // セッションにOIDC情報を保存
        val oidcSession = OidcSession(
            pkce = pkce,
            redirectUri = redirectUri.toString(),
            issuer = issuer.toString(),
            clientId = clientId
        )
        call.sessions.set(oidcSession)
    }

    suspend fun RoutingContext.respondRedirectIssuer(
        discoveryResponse: OidcDiscoveryResponse, clientId: String, redirectUri: Url, pkce: Pkce,
    ) {
        val authUrl = URLBuilder(discoveryResponse.authorizationEndpoint).apply {
            parameters.append("client_id", clientId)
            parameters.append("redirect_uri", redirectUri.toString())
            parameters.append("response_type", "code")
            parameters.append("scope", "openid profile email")
            parameters.append("state", pkce.state)
            parameters.append("nonce", pkce.nonce)
            parameters.append("code_challenge", pkce.codeChallenge)
            parameters.append("code_challenge_method", "S256")
        }.buildString()

        logger.info("Redirecting to authorization endpoint: $authUrl")

        call.respondRedirect(authUrl)
    }
}
