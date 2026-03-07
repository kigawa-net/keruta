package net.kigawa.keruta.ktcl.k8s.auth

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.domain.auth.key.PemKey
import net.kigawa.kodel.api.err.flatConvertOk
import net.kigawa.kodel.api.err.unwrap
import net.kigawa.kodel.api.log.LoggerFactory
import java.util.*

class AuthenticationHelper(
    private val auth0JwtVerifier: Auth0JwtVerifier,
    private val privateKey: PemKey,
) {
    private val logger = LoggerFactory.get("AuthenticationHelper")

    fun getAuthenticatedUser(call: ApplicationCall): UserSession? {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            logger.fine("No session found")
            return null
        }

        val expiresAt = try {
            JWT.decode(session.token).expiresAt
        } catch (e: Exception) {
            logger.fine("Failed to decode token: ${e.message}")
            call.sessions.clear<UserSession>()
            return null
        }

        if (expiresAt == null || expiresAt.before(Date())) {
            logger.fine("Token expired at $expiresAt, clearing session")
            call.sessions.clear<UserSession>()
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
