package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.auth.UserSession
import net.kigawa.kodel.api.log.LoggerFactory

class AuthRoutes(
    private val authenticationHelper: AuthenticationHelper,
) {
    private val logger = LoggerFactory.get("AuthRoutes")

    fun configure(route: Route) {
        route.route("/api/auth") {
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

                call.respond(
                    mapOf(
                        "userSubject" to user.userSubject,
                        "userIssuer" to user.userIssuer,
                        "userAudience" to user.userAudience,
                    )
                )
            }
        }
    }
}

