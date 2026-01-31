package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.kodel.api.log.LoggerFactory
import java.security.interfaces.RSAPublicKey

class JwtVerifier(
    val jwkProvider: JwkProvider,
    val keycloakConfig: KeycloakConfig,
) {
    private val logger = LoggerFactory.get("JwtVerifier")

    fun verify(token: String): String? {
        return try {
            val jwt = JWT.decode(token)
            val jwk = jwkProvider.get(jwt.keyId)
            val publicKey = jwk.publicKey as RSAPublicKey
            val algorithm = Algorithm.RSA256(publicKey, null)

            val verifier = JWT.require(algorithm)
                .withIssuer(keycloakConfig.issuer)
                .withAudience(keycloakConfig.audience)
                .build()

            val decodedJwt = verifier.verify(token)
            decodedJwt.subject
        } catch (e: Exception) {
            logger.severe("JWT verification failed: ${e.message}")
            null
        }
    }
}
