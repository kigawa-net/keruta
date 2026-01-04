package net.kigawa.keruta.ktcl.web.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.web.Config
import net.kigawa.keruta.ktcl.web.UserSession
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class RefreshRoute(val config: Config): WebRoute {
    override val info: EntrypointInfo
        get() = EntrypointInfo("refresh", listOf(), "Token refresh route")

    override fun Route.route() {
        post {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "No session")
                return@post
            }

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
                            "grant_type" to "refresh_token",
                            "client_id" to config.clientId,
                            "client_secret" to config.clientSecret,
                            "refresh_token" to session.refreshToken
                        ).formUrlEncode()
                    )
                }.body()

                val expiresAt = System.currentTimeMillis() + (tokenResponse.expires_in * 1000L)
                val newSession = UserSession(
                    accessToken = tokenResponse.access_token,
                    refreshToken = tokenResponse.refresh_token,
                    expiresAt = expiresAt
                )
                call.sessions.set(newSession)

                call.respond(mapOf("access_token" to tokenResponse.access_token))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, "Token refresh failed: ${e.message}")
            } finally {
                client.close()
            }
        }
    }
}
