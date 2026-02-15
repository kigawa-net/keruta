package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.err
import net.kigawa.kodel.api.err.ok
import java.security.interfaces.RSAPublicKey

class JwkUnverifiedTokenWithKey(
    private val decodedJwt: DecodedJWT,
    private val publicKey: RSAPublicKey,
) : UnverifiedTokenWithKey {
    override fun verify(verifyValues: JwtVerifyValues): Res<VerifiedToken, KtcpErr> {
        return try {
            val algorithm = Algorithm.RSA256(publicKey, null)
            val verifier = JWT.require(algorithm)
                .withIssuer(verifyValues.issuer.toString())
                .withAudience(verifyValues.audience)
                .build()
            val verified = verifier.verify(decodedJwt.token)
            JwkVerifiedToken(verified).ok()
        } catch (e: Exception) {
            VerifyErr("verify_failed", e.message, e).err()
        }
    }
}
