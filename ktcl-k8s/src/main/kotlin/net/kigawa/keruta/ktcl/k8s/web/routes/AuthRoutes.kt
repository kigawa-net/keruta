package net.kigawa.keruta.ktcl.k8s.web.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.keruta.ktcl.k8s.web.auth.getAuthenticatedUser
import net.kigawa.keruta.ktcl.k8s.web.auth.verifyToken
import net.kigawa.kodel.api.log.LoggerFactory

private val logger = LoggerFactory.get("AuthRoutes")

@Serializable
data class LoginRequest(
    val token: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val userId: String? = null,
    val message: String? = null
)

fun Route.configureAuthRoutes() {
    route("/api/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            logger.info("Login attempt")

            val userId = call.application.verifyToken(request.token)
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
            val user = call.getAuthenticatedUser()
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                return@get
            }

            call.respond(mapOf("userId" to user.userId))
        }
    }
}
