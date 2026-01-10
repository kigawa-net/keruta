package net.kigawa.keruta.ktse.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktcp.server.err.VerifyUnsupportedKeyErr
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.Verified
import net.kigawa.keruta.ktse.Config
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import java.net.URL
import java.security.interfaces.RSAPublicKey

class Auth0JwtVerifier(
    val config: Config,
): JwtVerifier {
    val jwkProvider: JwkProvider = JwkProviderBuilder(
        URL(
            config.issuer + "/protocol/openid-connect/certs"
        )
    ).build()
    val logger = getKogger()
    override fun verify(
        token: AuthToken,
    ): Res<Verified, VerifyErr> {
        val decoded = JWT.decode(token)
        logger.debug("decoded: ${decoded.audience}")
        val key = jwkProvider.get(decoded.keyId)
        val alg = when (val pub = key.publicKey) {
            is RSAPublicKey -> Algorithm.RSA256(pub, null)
            else -> return Res.Err(VerifyUnsupportedKeyErr(pub.toString(), null))
        }
        val verifier = JWT.require(alg)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
        return try {
            val verified = verifier.verify(token)
            Res.Ok(Auth0Verified(verified))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("",e))
        }
    }
}
