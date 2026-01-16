package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.Idp
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.auth.UnverifiedToken
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.kodel.api.err.Res

class Auth0UnverifiedToken(
    val decode: DecodedJWT,
    val verifier: Auth0JwtVerifier,
    val strToken: AuthToken,
): UnverifiedToken {
    override suspend fun verify(
        idp: Idp,
        oidc: Boolean,
    ): Res<VerifiedToken, KtcpErr> = verifier.verify(
        strToken, decode, idp.subject,
        IdpConfig(idp.issuer, idp.audience),
        oidc
    )

    override val subject: String
        get() = decode.subject
    override val issuer: String
        get() = decode.issuer
}
