package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProvider
import io.ktor.server.application.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.kodel.api.log.LoggerFactory

class AuthGuard(jwkProvider: JwkProvider, keycloakConfig: KeycloakConfig) {
    private val logger = LoggerFactory.get("AuthGuard")
    private val authenticationHelper = AuthenticationHelper(jwkProvider, keycloakConfig)

    suspend fun requireAuth(call: ApplicationCall, block: suspend (UserSession) -> Unit) {
        logger.fine("Checking authentication for request: ${call.request.local.uri}")
        authenticationHelper.requireAuth(call, block)
    }

}
