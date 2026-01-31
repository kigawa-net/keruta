package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.kodel.api.log.LoggerFactory

class AuthenticationHelper(jwkProvider: JwkProvider, keycloakConfig: KeycloakConfig) {
    private val logger = LoggerFactory.get("AuthenticationHelper")
    private val jwtVerifier = JwtVerifier(jwkProvider, keycloakConfig)

    fun getAuthenticatedUser(call: ApplicationCall): UserSession? {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            logger.fine("No session found")
            return null
        }

        val userId = jwtVerifier.verify(session.token)
        if (userId == null) {
            logger.fine("Token verification failed")
            call.sessions.clear<UserSession>()
            return null
        }

        return session
    }

    suspend fun requireAuth(call: ApplicationCall, block: suspend (UserSession) -> Unit) {
        val user = getAuthenticatedUser(call)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
            return
        }
        block(user)
    }
}
