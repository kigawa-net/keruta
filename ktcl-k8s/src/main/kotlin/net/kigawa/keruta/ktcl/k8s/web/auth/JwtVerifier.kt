package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
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
                .withIssuer(keycloakConfig.issuer.toString())
                .withAudience(keycloakConfig.audience)
                .build()

            val decodedJwt = verifier.verify(token)
            decodedJwt.subject
        } catch (e: Exception) {
            logger.severe("JWT verification failed: ${e.message}")
            null
        }
    }

    fun verifyIdToken(
        idToken: String,
        jwkProvider: JwkProvider,
        issuer: String,
        clientId: String,
        nonce: String?,
    ): DecodedJWT? {
        return try {
            val jwt = JWT.decode(idToken)
            val jwk = jwkProvider.get(jwt.keyId)
            val publicKey = jwk.publicKey as RSAPublicKey
            val algorithm = Algorithm.RSA256(publicKey, null)

            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(clientId)
                .apply {
                    if (nonce != null) {
                        withClaim("nonce", nonce)
                    }
                }
                .build()

            verifier.verify(idToken)
        } catch (e: Exception) {
            logger.severe("ID token verification failed: ${e.message}")
            null
        }
    }
}
