package net.kigawa.keruta.ktcl.web.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.web.Config
import net.kigawa.keruta.ktcl.web.UserSession
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

@Serializable
data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val token_type: String,
)

class CallbackRoute(val config: Config): WebRoute {
    override val info: EntrypointInfo
        get() = EntrypointInfo("callback", listOf(), "")

    override fun Route.route() {
        get {
            val code = call.request.queryParameters["code"]
            if (code != null) {
                val baseUrl = call.request.origin.run {
                    "$scheme://$serverHost${if (serverPort == 80 || serverPort == 443) "" else ":$serverPort"}"
                }
                val redirectUri = "${baseUrl}/auth/callback"
                val tokenUrl = "${config.issuer}/protocol/openid-connect/token"

                val client = HttpClient(CIO) {
                    install(ContentNegotiation) {
                        json()
                    }
                }

                try {
                    val tokenResponse: TokenResponse = client.post(tokenUrl) {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            listOf(
                                "grant_type" to "authorization_code",
                                "client_id" to config.clientId,
                                "client_secret" to config.clientSecret,
                                "code" to code,
                                "redirect_uri" to redirectUri
                            ).formUrlEncode()
                        )
                    }.body()

                    val expiresAt = System.currentTimeMillis() + (tokenResponse.expires_in * 1000L)
                    val session = UserSession(
                        accessToken = tokenResponse.access_token,
                        refreshToken = tokenResponse.refresh_token,
                        expiresAt = expiresAt
                    )
                    call.sessions.set(session)

                    call.respondHtml {
                        head {
                            title { +"Login Success" }
                        }
                        body {
                            h1 { +"Login Successful!" }
                            p { +"Access Token: ${tokenResponse.access_token.take(20)}..." }
                            a(href = "/") { +"Go to Home" }
                        }
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Unauthorized, "Token exchange failed: ${e.message}")
                } finally {
                    client.close()
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Authentication failed")
            }
        }
    }
}
