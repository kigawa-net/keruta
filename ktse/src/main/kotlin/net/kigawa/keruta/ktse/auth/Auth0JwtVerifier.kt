package net.kigawa.keruta.ktse.auth

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Verification
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.auth.VerifyConfig
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktcp.server.err.VerifyUnsupportedKeyErr
import net.kigawa.kodel.api.err.Res
import java.net.URL
import java.security.interfaces.RSAPublicKey

class Auth0JwtVerifier(
    private val verifyConfig: VerifyConfig,
): JwtVerifier {
    @Suppress("DEPRECATION")
    private val userProvider: JwkProvider = JwkProviderBuilder(URL(verifyConfig.jwksUrl)).build()

    override fun verifyUserToken(
        token: AuthToken,
    ): Res<VerifiedToken, VerifyErr> {
        val decoded = JWT.decode(token)
        return verify(token, userProvider, verifyConfig.issuer, decoded) { it }
    }

    override fun verifyServerToken(
        token: String,
        subject: String,
    ): Res<VerifiedToken, VerifyErr> {
        val decoded = JWT.decode(token)
        val provider = try {
            JwkProviderBuilder(decoded.issuer).build()
        } catch (e: IllegalStateException) {
            return Res.Err(VerifyFailErr("iss: ${decoded.issuer}", e))
        }
        return verify(token, provider, decoded.issuer, decoded) { it.withSubject(subject) }
    }

    private fun verify(
        token: AuthToken,
        provider: JwkProvider,
        issuer: String,
        rawToken: DecodedJWT,
        configureVerifier: (Verification) -> Verification,
    ): Res<VerifiedToken, VerifyErr> {
        val key = provider.get(rawToken.keyId)
        val alg = when (val alg = alg(key)) {
            is Res.Err<*, VerifyUnsupportedKeyErr> -> return alg.convertType()
            is Res.Ok<Algorithm, *> -> alg.value
        }
        val verifier = verifier(alg, issuer, verifyConfig.audience, configureVerifier)
        return try {
            val verified = verifier.verify(token)
            Res.Ok(Auth0VerifiedToken(verified))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("", e))
        }
    }

    private fun verifier(
        alg: Algorithm, issuer: String, audience: String,
        configureVerifier: (Verification) -> Verification,
    ) = JWT.require(alg)
        .withIssuer(issuer)
        .withAudience(audience)
        .let { configureVerifier(it) }
        .build()

    private fun alg(key: Jwk): Res<Algorithm, VerifyUnsupportedKeyErr> = when (
        val pub = key.publicKey
    ) {
        is RSAPublicKey -> Res.Ok(Algorithm.RSA256(pub, null))
        else -> Res.Err(VerifyUnsupportedKeyErr(pub.toString(), null))
    }

}
