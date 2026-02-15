package net.kigawa.keruta.ktse.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifiedToken
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.ok

class Auth0UnverifiedTokenWithKey(
    val unverifiedToken: Auth0UnverifiedToken, val algorithm: Algorithm,
): UnverifiedTokenWithKey {
    override fun verify(
        verifyValues: JwtVerifyValues,
    ): Res<VerifiedToken, KtcpErr> = try {
        JWT.require(algorithm)
            .withIssuer(verifyValues.issuer.toStrUrl())
            .withAudience(verifyValues.audience)
            .withSubject(verifyValues.subject)
            .acceptExpiresAt(0)
            .build()
            .verify(unverifiedToken.strToken)
            .let { Auth0VerifiedToken(it) }
            .ok()
    } catch (e: Exception) {
        return Res.Err(VerifyFailErr("", e))
    }

}
