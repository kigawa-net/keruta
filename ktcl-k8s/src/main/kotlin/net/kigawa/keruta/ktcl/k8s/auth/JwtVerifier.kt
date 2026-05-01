package net.kigawa.keruta.ktcl.k8s.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.domain.auth.AuthToken
import net.kigawa.keruta.ktcp.domain.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.domain.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.domain.auth.jwt.VerifyErr
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.net.Url
import java.security.interfaces.RSAPublicKey
import net.kigawa.keruta.ktcp.domain.auth.jwt.JwtVerifier as KtcpJwtVerifier

class JwtVerifier(
    val keycloakConfig: KeycloakConfig,
    private val auth0JwtVerifier: Auth0JwtVerifier,
): KtcpJwtVerifier {
    private val logger = LoggerFactory.get("JwtVerifier")

    override fun createToken(jwtVerifyValues: JwtVerifyValues): Res<AuthToken, KtcpErr> =
        Res.Err(VerifyErr("not_supported", "Token creation is not supported in web JwtVerifier", null))

    override fun decodeUnverified(userToken: AuthToken): Res<UnverifiedToken, VerifyErr> =
        auth0JwtVerifier.decodeUnverified(userToken)

    fun verify(token: String): String? {
        val verifyValues = JwtVerifyValues(
            issuer = Url.parse(keycloakConfig.issuer.toString()),
            // 空の場合はデフォルトで issuer を使用
            audience = keycloakConfig.audience.ifEmpty { keycloakConfig.issuer.toString() },
            subject = "",
        )
        return when (val unverified = decodeUnverified(token)) {
            is Res.Err -> {
                logger.severe("JWT verification failed: ${unverified.err.message}")
                null
            }

            is Res.Ok -> when (val verified = unverified.value.verifyWithJwks(verifyValues)) {
                is Res.Err -> {
                    logger.severe("JWT verification failed: ${verified.err.message}")
                    null
                }

                is Res.Ok -> verified.value.subject
            }
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
