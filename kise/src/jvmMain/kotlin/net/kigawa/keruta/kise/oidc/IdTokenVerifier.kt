package net.kigawa.keruta.kise.oidc

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.kise.oidc.model.OidcDiscoveryResponse
import net.kigawa.keruta.kise.oidc.model.OidcSession
import net.kigawa.kodel.api.log.LoggerFactory
import java.util.concurrent.TimeUnit

class IdTokenVerifier {
    private val logger = LoggerFactory.get("IdTokenVerifier")

    fun verify(
        idToken: String,
        discoveryResponse: OidcDiscoveryResponse,
        oidcSession: OidcSession,
    ): String? = try {
        // JWKSエンドポイントから公開鍵を取得
        val jwkProvider: JwkProvider = JwkProviderBuilder(discoveryResponse.jwksUri)
            .cached(10, 24, TimeUnit.HOURS)
            .build()

        val decodedJWT: DecodedJWT = JWT.decode(idToken)
        val jwk = jwkProvider.get(decodedJWT.keyId)

        val algorithm = when (jwk.algorithm) {
            "RS256" -> Algorithm.RSA256(jwk.publicKey as java.security.interfaces.RSAPublicKey, null)
            "RS384" -> Algorithm.RSA384(jwk.publicKey as java.security.interfaces.RSAPublicKey, null)
            "RS512" -> Algorithm.RSA512(jwk.publicKey as java.security.interfaces.RSAPublicKey, null)
            else -> throw IllegalArgumentException("Unsupported algorithm: ${jwk.algorithm}")
        }

        val verifier = JWT.require(algorithm)
            .withIssuer(oidcSession.issuer)
            .withAudience(oidcSession.clientId)
            .withClaim("nonce", oidcSession.pkce.nonce)
            .build()

        val verifiedJWT = verifier.verify(idToken)

        // ユーザーのsubject（ユニークID）を返す
        verifiedJWT.subject
    } catch (e: Exception) {
        logger.severe("Failed to verify ID token: ${e.message}")
        null
    }
}
