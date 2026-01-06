package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.server.authenticate.Verified

class Auth0Verified(verified: DecodedJWT): Verified {
}
