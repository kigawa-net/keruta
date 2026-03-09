package net.kigawa.keruta.ktcp.domain.auth.jwt

import net.kigawa.keruta.ktcp.domain.auth.AuthToken
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface JwtVerifier {
    fun decodeUnverified(userToken: AuthToken): Res<UnverifiedToken, VerifyErr>
    fun createToken(jwtVerifyValues: JwtVerifyValues): Res<AuthToken, KtcpErr>
}
