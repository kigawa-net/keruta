package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken

class Auth0VerifiedToken(
    val verified: DecodedJWT,
): VerifiedToken {
    override val subject: String
        get() = verified.subject
}
