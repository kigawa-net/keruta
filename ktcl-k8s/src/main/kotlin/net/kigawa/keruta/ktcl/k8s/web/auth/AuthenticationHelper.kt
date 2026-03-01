package net.kigawa.keruta.ktcl.k8s.web.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.model.auth.key.PrivateKey
import net.kigawa.kodel.api.err.flatConvertOk
import net.kigawa.kodel.api.err.unwrap
import net.kigawa.kodel.api.log.LoggerFactory

class AuthenticationHelper(
    private val auth0JwtVerifier: Auth0JwtVerifier,
    private val privateKey: PrivateKey,
) {
    private val logger = LoggerFactory.get("AuthenticationHelper")

    fun getAuthenticatedUser(call: ApplicationCall): UserSession? {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            logger.fine("No session found")
            return null
        }

        auth0JwtVerifier.decodeUnverified(session.token)
            .flatConvertOk {
                it.withKey(privateKey)
            }.unwrap {
                call.sessions.clear<UserSession>()
                throw IllegalStateException("Token verification failed", it)
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
