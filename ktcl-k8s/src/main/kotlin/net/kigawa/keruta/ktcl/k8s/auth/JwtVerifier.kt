package net.kigawa.keruta.ktcl.k8s.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifier as KtcpJwtVerifier
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.err
import net.kigawa.kodel.api.err.ok
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.net.Url
import java.security.interfaces.RSAPublicKey

class JwtVerifier(
    val jwkProvider: JwkProvider,
    val keycloakConfig: KeycloakConfig,
) : KtcpJwtVerifier {
    private val logger = LoggerFactory.get("JwtVerifier")

    override fun createToken(): Res<AuthToken, KtcpErr> =
        Res.Err(VerifyErr("not_supported", "Token creation is not supported in web JwtVerifier", null))

    override fun decodeUnverified(userToken: AuthToken): Res<UnverifiedToken, VerifyErr> {
        return try {
            val jwt = JWT.decode(userToken)
            JwkUnverifiedToken(jwt, jwkProvider).ok()
        } catch (e: Exception) {
            logger.severe("JWT decode failed: ${e.message}")
            VerifyErr("decode_failed", e.message, e).err()
        }
    }

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
