package net.kigawa.keruta.ktse.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.model.auth.AuthenticateToken
import net.kigawa.keruta.ktcp.model.err.types.VerifyErr
import net.kigawa.keruta.ktcp.model.err.types.VerifyFailErr
import net.kigawa.keruta.ktcp.model.err.types.VerifyUnsupportedKeyErr
import net.kigawa.keruta.ktcp.server.authenticate.JwtVerifier
import net.kigawa.keruta.ktcp.server.authenticate.Verified
import net.kigawa.keruta.ktse.Config
import net.kigawa.kodel.api.err.Res
import java.security.interfaces.RSAPublicKey

class Auth0JwtVerifier(
    val config: Config,
): JwtVerifier {
    val jwkProvider: JwkProvider = JwkProviderBuilder(config.issuer).build()
    override fun verify(
        token: AuthenticateToken,
    ): Res<Verified, VerifyErr> {
        val key = jwkProvider.get(JWT.decode(token).keyId)
        val alg = when (val pub = key.publicKey) {
            is RSAPublicKey -> Algorithm.RSA256(pub, null)
            else -> return Res.Err(VerifyUnsupportedKeyErr(pub.toString()))
        }
        val verifier = JWT.require(alg)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
        return try {
            val verified = verifier.verify(token)
            Res.Ok(Auth0Verified(verified))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr(e))
        }
    }
}
