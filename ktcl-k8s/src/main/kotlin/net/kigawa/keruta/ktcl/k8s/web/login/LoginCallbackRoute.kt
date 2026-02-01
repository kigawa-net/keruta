package net.kigawa.keruta.ktcl.k8s.web.login

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.JwtVerifier
import net.kigawa.keruta.ktcl.k8s.web.auth.KeycloakConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.OidcDiscoveryFetcher
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URI

class LoginCallbackRoute(
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher = OidcDiscoveryFetcher()
) {
    private val logger = LoggerFactory.get("LoginCallbackRoute")
    private val authConfig = AuthConfig(oidcDiscoveryFetcher)

    @Serializable
    data class TokenResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("id_token") val idToken: String? = null,
        @SerialName("token_type") val tokenType: String,
        @SerialName("expires_in") val expiresIn: Int? = null,
    )

    fun configure(route: Route) = route.get("/login/callback") {
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

        // セッションからOIDC情報を取得
        val oidcSession = call.sessions.get<OidcSession>()
        if (oidcSession == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No OIDC session found"))
            return@get
        }

        // stateの検証
        if (oidcSession.pkce.state != state) {
            logger.warning("State mismatch: expected ${oidcSession.pkce.state}, got $state")
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid state parameter"))
            return@get
        }

        logger.info("Processing OIDC callback for issuer: ${oidcSession.issuer}")

        try {
            // OIDC Discoveryエンドポイントから設定を取得
            val wellKnownUrl = "${oidcSession.issuer}/.well-known/openid-configuration"
            val discoveryResponse = oidcDiscoveryFetcher.fetch(wellKnownUrl)

            if (discoveryResponse.tokenEndpoint == null) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Token endpoint not found in OIDC discovery")
                )
                return@get
            }

            // トークンエンドポイントで認可コードをトークンに交換
            val tokenResponse = exchangeCodeForToken(
                tokenEndpoint = discoveryResponse.tokenEndpoint,
                code = code,
                redirectUri = oidcSession.redirectUri,
                clientId = oidcSession.clientId,
                codeVerifier = oidcSession.pkce.codeVerifier
            )

            // IDトークンの検証
            val idToken = tokenResponse.idToken
            if (idToken == null) {
                logger.warning("No ID token received from token endpoint")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "No ID token received")
                )
                return@get
            }

            // JWKプロバイダーを作成
            val jwkProvider = authConfig.createJwkProvider(discoveryResponse.jwksUri)

            // IDトークンを検証
            val keycloakConfig = KeycloakConfig(
                audience = oidcSession.clientId,
                jwksUrl = discoveryResponse.jwksUri,
                issuer = URI(oidcSession.issuer)
            )
            val jwtVerifier = JwtVerifier(jwkProvider, keycloakConfig)
            val decodedJwt = jwtVerifier.verifyIdToken(
                idToken = idToken,
                jwkProvider = jwkProvider,
                issuer = oidcSession.issuer,
                clientId = oidcSession.clientId,
                nonce = oidcSession.pkce.nonce
            )

            if (decodedJwt == null) {
                logger.warning("ID token verification failed")
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Invalid ID token")
                )
                return@get
            }

            val userId = decodedJwt.subject
            if (userId == null) {
                logger.warning("No subject found in ID token")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "No user ID in token")
                )
                return@get
            }

            // OIDCセッションをクリア
            call.sessions.clear<OidcSession>()

            // UserSessionを作成
            val userSession = UserSession(
                userId = userId,
                token = tokenResponse.accessToken
            )
            call.sessions.set(userSession)

            logger.info("Login successful for user: $userId, redirecting to home page")

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

    private suspend fun exchangeCodeForToken(
        tokenEndpoint: String,
        code: String,
        redirectUri: String,
        clientId: String,
        codeVerifier: String
    ): TokenResponse {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        return client.use { client ->
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

            response.body<TokenResponse>()
        }
    }
}
