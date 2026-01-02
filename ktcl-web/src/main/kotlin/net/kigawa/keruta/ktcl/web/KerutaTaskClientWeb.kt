package net.kigawa.keruta.ktcl.web

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.web.module.JwtModule
import net.kigawa.keruta.ktcl.web.module.WebsocketModule
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.kodel.api.log.getKogger
import java.util.*

@Serializable
data class LoginRequest(val username: String, val password: String)

class KerutaTaskClientWeb(val application: Application) {
    val ktcpClient = KtcpClient()
    val logger = getKogger()
    val config = Config.load(application.environment.config)
    val webEntrypoints = WebEntrypoints()
    fun module() = application.apply {
        WebsocketModule.module(application)
        JwtModule.module(application, config)
        install(ContentNegotiation) {
            json()
        }
        routing {
            webEntrypoints.flat().forEach {
                route(it.path.joinToString(separator = "/", prefix = "/")) {
                    runBlocking { it.access(this@routing, Unit) }
                }
            }
            get("/auth/keycloak") {
                val baseUrl = call.request.origin.run {
                    "$scheme://$serverHost${if (serverPort == 80 || serverPort == 443) "" else ":$serverPort"}"
                }
                val redirectUri = "${baseUrl}/auth/callback"
                val authorizeUrl = "${config.issuer}/realms/${config.realm}/protocol/openid-connect/auth?client_id=${config.clientId}&redirect_uri=$redirectUri&response_type=code&scope=openid"
                call.respondRedirect(authorizeUrl)
            }
            get("/auth/callback") {
                val code = call.request.queryParameters["code"]
                if (code != null) {
                    // Here you would exchange the code for access token
                    // For now, just show success
                    call.respondHtml {
                        head {
                            title { +"Login Success" }
                        }
                        body {
                            h1 { +"Login Successful!" }
                            p { +"Authorization Code: $code" }
                            a(href = "/") { +"Go to Home" }
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Authentication failed")
                }
            }
            post("/login") {
                val request = call.receive<LoginRequest>()
                if (request.username == "admin" && request.password == "password") {
                    val algorithm = Algorithm.HMAC256(config.clientSecret)
                    val token = JWT.create()
                        .withAudience(config.audience)
                        .withIssuer(config.issuer)
                        .withClaim("username", request.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(algorithm)
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            }
            authenticate("auth-jwt") {

            }
        }
    }

    companion object {
        fun Application.module() {
            KerutaTaskClientWeb(this).module()
        }
    }
}
