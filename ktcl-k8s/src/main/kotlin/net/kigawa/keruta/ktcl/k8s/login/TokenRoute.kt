package net.kigawa.keruta.ktcl.k8s.login

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.config.IdpConfig
import net.kigawa.keruta.ktcl.k8s.auth.OidcDiscoveryFetcher
import net.kigawa.kodel.api.log.LoggerFactory

/**
 * OIDC Tokenエンドポイント
 * KTCL-K8sがOIDC Providerとして動作する際のトークン交換エンドポイント
 * リクエストを下位のIdP（Keycloakなど）に転送する
 */
class TokenRoute(
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher,
    val idpConfig: IdpConfig
) {
    private val logger = LoggerFactory.get("TokenRoute")

    fun configure(route: Route) {
        route.post("/protocol/openid-connect/token") {
            val params = call.receiveParameters()
            val grantType = params["grant_type"]
            val code = params["code"]
            val redirectUri = params["redirect_uri"]
            val clientId = params["client_id"]

            logger.info("Token request received: grant_type=$grantType, client_id=$clientId")

            if (grantType != "authorization_code") {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "unsupported_grant_type", "error_description" to "Only authorization_code is supported")
                )
                return@post
            }

            if (code == null || redirectUri == null || clientId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "invalid_request", "error_description" to "Missing required parameters")
                )
                return@post
            }

            try {
                // 下位IdPのdiscovery情報を取得
                val discoveryResponse = oidcDiscoveryFetcher.fetchByIssuer(idpConfig.issuer)
                val tokenEndpoint = discoveryResponse.tokenEndpoint
                    ?: throw IllegalStateException("Token endpoint not found in OIDC discovery")

                // 下位IdPにトークンリクエストを転送
                val tokenResponse = exchangeTokenWithIdp(
                    tokenEndpoint = tokenEndpoint,
                    code = code,
                    redirectUri = redirectUri,
                    clientId = clientId
                )

                call.respondText(tokenResponse, ContentType.Application.Json)
            } catch (e: Exception) {
                logger.severe("Token exchange failed: ${e.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "server_error", "error_description" to e.message)
                )
            }
        }
    }

    private suspend fun exchangeTokenWithIdp(
        tokenEndpoint: String,
        code: String,
        redirectUri: String,
        clientId: String
    ): String {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        return client.use { httpClient ->
            val response = httpClient.submitForm(
                url = tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("code", code)
                    append("redirect_uri", redirectUri)
                    append("client_id", clientId)
                }
            )
            response.body<String>()
        }
    }
}
