package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.server.auth.Verified

class Auth0Verified(
    val verified: DecodedJWT,
): Verified {
    override val sub: String
        get() = verified.subject
}
