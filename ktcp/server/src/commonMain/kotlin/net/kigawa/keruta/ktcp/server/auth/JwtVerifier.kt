package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.server.types.VerifyErr
import net.kigawa.kodel.api.err.Res

interface JwtVerifier {
    fun verify(token: AuthToken): Res<Verified, VerifyErr>
}
