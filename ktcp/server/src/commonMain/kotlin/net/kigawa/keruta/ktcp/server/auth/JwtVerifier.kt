package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.kodel.api.err.Res

interface JwtVerifier {
    fun decodeUnverified(userToken: AuthToken): Res<UnverifiedToken, VerifyErr>
}
