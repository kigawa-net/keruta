package net.kigawa.keruta.kise.route

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.kise.KiseConfig
import net.kigawa.keruta.kise.oidc.IdTokenVerifier
import net.kigawa.keruta.kise.oidc.OidcDiscoveryFetcher
import net.kigawa.keruta.kise.oidc.model.OidcSession
import net.kigawa.keruta.kise.oidc.model.TokenResponse
import net.kigawa.kodel.api.log.LoggerFactory
import java.io.InvalidObjectException

class CallbackRoute(
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher,
    private val idTokenVerifier: IdTokenVerifier,
    private val config: KiseConfig,
) {
    private val logger = LoggerFactory.get("CallbackRoute")

    fun configure(route: Route) = route.get("/callback") {
        val code = call.parameters["code"]
        val state = call.parameters["state"]
        val error = call.parameters["error"]

        // エラーレスポンスの処理
        if (error != null) {
            val errorDescription = call.parameters["error_description"]
            logger.warning("OIDC error: $error - $errorDescription")
            call.respondRedirect("/?error=${error.encodeURLParameter()}")
            return@get
        }

        // codeとstateの検証
        if (code == null || state == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing code or state parameter"))
            return@get
        }

        val oidcSession = getValidatedOidcSession(call, state) ?: return@get

        logger.info(
            "Processing OIDC callback for issuer: ${oidcSession.issuer}, clientId: ${oidcSession.clientId}"
        )

        processOidcCallback(call, code, oidcSession)
    }

    private suspend fun processOidcCallback(call: ApplicationCall, code: String, oidcSession: OidcSession) {
        try {
            // OIDC Discoveryエンドポイントから設定を取得
            val wellKnownUrl = "${oidcSession.issuer}/.well-known/openid-configuration"
            val discoveryResponse = oidcDiscoveryFetcher.fetch(wellKnownUrl)

            if (discoveryResponse.tokenEndpoint == null) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Token endpoint not found in OIDC discovery")
                )
                return
            }

            // トークンエンドポイントで認可コードをトークンに交換
            val tokenResponse = exchangeCodeForToken(
                tokenEndpoint = discoveryResponse.tokenEndpoint,
                code = code,
                redirectUri = oidcSession.redirectUri,
                clientId = oidcSession.clientId,
                codeVerifier = oidcSession.pkce.codeVerifier
            )

            val idToken = tokenResponse.idToken
            if (idToken == null) {
                logger.warning("No ID token received from token endpoint")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "No ID token received")
                )
                return
            }

            val userSubject = idTokenVerifier.verify(idToken, discoveryResponse, oidcSession)
            if (userSubject == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid ID token"))
                return
            }

            logger.info("Login successful for user: $userSubject (issuer: ${oidcSession.issuer})")

            // フロントエンドにリダイレクト
            call.respondRedirect("/")

        } catch (e: Exception) {
            logger.severe("Failed to process OIDC callback: ${e.message}")
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Failed to complete login", "message" to e.message)
            )
        }
    }

    private suspend fun getValidatedOidcSession(call: ApplicationCall, state: String): OidcSession? {
        val oidcSession = call.sessions.get<OidcSession>()
        if (oidcSession == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No OIDC session found"))
            return null
        }
        if (oidcSession.pkce.state != state) {
            logger.warning("State mismatch: expected ${oidcSession.pkce.state}, got $state")
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid state parameter"))
            return null
        }
        return oidcSession
    }

    private suspend fun exchangeCodeForToken(
        tokenEndpoint: String,
        code: String,
        redirectUri: String,
        clientId: String,
        codeVerifier: String,
    ): TokenResponse {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        return client.use { client ->
            logger.info(
                "Exchanging code for token: $code, redirectUri: $redirectUri, clientId: $clientId"
            )
            val response = client.submitForm(
                url = tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("code", code)
                    append("redirect_uri", redirectUri)
                    append("client_id", clientId)
                    append("code_verifier", codeVerifier)
                }
            )

            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                throw IllegalStateException("Token exchange failed (${response.status.value}): $errorBody")
            }
            response.body<TokenResponse>().also {
                logger.info("Token response received successfully")
            }
        }
    }
}
