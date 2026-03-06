package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface JwtVerifier {
    fun decodeUnverified(userToken: AuthToken): Res<UnverifiedToken, VerifyErr>
    fun createToken(jwtVerifyValues: JwtVerifyValues): Res<AuthToken, KtcpErr>
}
