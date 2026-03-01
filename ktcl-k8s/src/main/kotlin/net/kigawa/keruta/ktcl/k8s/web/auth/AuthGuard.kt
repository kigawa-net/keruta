package net.kigawa.keruta.ktcl.k8s.web.auth

import io.ktor.server.application.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.model.auth.key.PrivateKey
import net.kigawa.kodel.api.log.LoggerFactory

class AuthGuard(
    auth0JwtVerifier: Auth0JwtVerifier,
    privateKey: PrivateKey,
) {
    private val logger = LoggerFactory.get("AuthGuard")
    private val authenticationHelper = AuthenticationHelper(auth0JwtVerifier, privateKey)

    suspend fun requireAuth(call: ApplicationCall, block: suspend (UserSession) -> Unit) {
        logger.fine("Checking authentication for request: ${call.request.local.uri}")
        authenticationHelper.requireAuth(call, block)
    }

}
