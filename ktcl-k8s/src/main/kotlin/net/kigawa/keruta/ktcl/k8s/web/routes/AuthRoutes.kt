package net.kigawa.keruta.ktcl.k8s.web.routes

import com.auth0.jwk.JwkProvider
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.web.auth.JwtVerifier
import net.kigawa.keruta.ktcl.k8s.web.auth.KeycloakConfig
import net.kigawa.kodel.api.log.LoggerFactory

class AuthRoutes(
    jwkProvider: JwkProvider,
    keycloakConfig: KeycloakConfig
) {
    private val logger = LoggerFactory.get("AuthRoutes")
    private val jwtVerifier = JwtVerifier(jwkProvider, keycloakConfig)
    private val authenticationHelper = AuthenticationHelper(jwkProvider, keycloakConfig)

    fun configure(route: Route) {
        route.route("/api/auth") {
            post("/login") {
                val request = call.receive<LoginRequest>()
                logger.info("Login attempt")

                val userId = jwtVerifier.verify(request.token)
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        LoginResponse(false, message = "Invalid token")
                    )
                    return@post
                }

                val session = UserSession(userId = userId, token = request.token)
                call.sessions.set(session)

                logger.info("Login successful: $userId")
                call.respond(LoginResponse(success = true, userId = userId))
            }

            post("/logout") {
                call.sessions.clear<UserSession>()
                logger.info("Logout successful")
                call.respond(mapOf("success" to true))
            }

            get("/me") {
                val user = authenticationHelper.getAuthenticatedUser(call)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@get
                }

                call.respond(mapOf("userId" to user.userId))
            }
        }
    }
}

