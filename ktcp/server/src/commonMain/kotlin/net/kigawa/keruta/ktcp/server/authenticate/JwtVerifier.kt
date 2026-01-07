package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.auth.AuthenticateToken
import net.kigawa.keruta.ktcp.model.err.types.VerifyErr
import net.kigawa.kodel.api.err.Res

interface JwtVerifier {
    fun verify(token: AuthenticateToken): Res<Verified, VerifyErr>
}
