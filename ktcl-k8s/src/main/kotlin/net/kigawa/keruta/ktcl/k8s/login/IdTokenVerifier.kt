package net.kigawa.keruta.ktcl.k8s.login

import net.kigawa.keruta.ktcl.k8s.auth.JwtVerifier
import net.kigawa.keruta.ktcl.k8s.auth.KeycloakConfig
import net.kigawa.keruta.ktcl.k8s.auth.OidcDiscoveryResponse
import net.kigawa.keruta.ktcl.k8s.auth.RemoteConfigProvider
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URI

class IdTokenVerifier(
    private val remoteConfigProvider: RemoteConfigProvider,
) {
    private val logger = LoggerFactory.get("IdTokenVerifier")

    fun verify(idToken: String, discoveryResponse: OidcDiscoveryResponse, oidcSession: OidcSession): String? {
        val jwkProvider = remoteConfigProvider.createJwkProvider(discoveryResponse.jwksUri)

        val keycloakConfig = KeycloakConfig(
            audience = oidcSession.clientId,
            jwksUrl = discoveryResponse.jwksUri,
            issuer = URI(oidcSession.issuer),
            authorizationEndpoint = discoveryResponse.authorizationEndpoint,
        )
        val jwtVerifier = JwtVerifier(jwkProvider, keycloakConfig)
        val decodedJwt = jwtVerifier.verifyIdToken(
            idToken = idToken,
            jwkProvider = jwkProvider,
            issuer = oidcSession.issuer,
            clientId = oidcSession.clientId,
            nonce = oidcSession.pkce.nonce,
        )

        if (decodedJwt == null) {
            logger.warning("ID token verification failed")
            return null
        }

        val userId = decodedJwt.subject
        if (userId == null) {
            logger.warning("No subject found in ID token")
            return null
        }

        return userId
    }
}
