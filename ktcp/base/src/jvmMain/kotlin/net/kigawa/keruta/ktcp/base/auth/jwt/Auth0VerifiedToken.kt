package net.kigawa.keruta.ktcp.base.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifiedToken
import net.kigawa.kodel.api.net.Url

class Auth0VerifiedToken(
    val verified: DecodedJWT,
): VerifiedToken {
    override val audience: String
        get() = verified.audience.first()
    override val issuer: Url
        get() = Url.parse(verified.issuer)
    override val subject: String by verified::subject
}
