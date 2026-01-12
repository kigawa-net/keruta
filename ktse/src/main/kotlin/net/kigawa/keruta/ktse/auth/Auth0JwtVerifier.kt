package net.kigawa.keruta.ktse.auth

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.auth.VerifyConfig
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktcp.server.err.VerifyUnsupportedKeyErr
import net.kigawa.kodel.api.err.Res
import java.security.interfaces.RSAPublicKey

class Auth0JwtVerifier: JwtVerifier {

    override fun verify(
        token: AuthToken,
        verifyConfig: VerifyConfig,
    ): Res<VerifiedToken, VerifyErr> {
        val provider = provider(verifyConfig)
        val decoded = JWT.decode(token)
        val key = provider.get(decoded.keyId)
        val alg = when (val alg = alg(key)) {
            is Res.Err<*, VerifyUnsupportedKeyErr> -> return alg.convertType()
            is Res.Ok<Algorithm, *> -> alg.value
        }
        val verifier = verifier(verifyConfig, alg)
        return try {
            val verified = verifier.verify(token)
            Res.Ok(Auth0VerifiedToken(verified))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("", e))
        }
    }

    private fun verifier(verifyConfig: VerifyConfig, alg: Algorithm) = JWT.require(alg)
        .withIssuer(verifyConfig.issuer)
        .withAudience(verifyConfig.audience)
        .build()

    private fun alg(key: Jwk): Res<Algorithm, VerifyUnsupportedKeyErr> = when (
        val pub = key.publicKey
    ) {
        is RSAPublicKey -> Res.Ok(Algorithm.RSA256(pub, null))
        else -> Res.Err(VerifyUnsupportedKeyErr(pub.toString(), null))
    }

    private fun provider(verifyConfig: VerifyConfig): JwkProvider = verifyConfig.jwksUrl
        ?.let { JwkProviderBuilder(it).build() }
        ?: JwkProviderBuilder(verifyConfig.issuer).build()

}
