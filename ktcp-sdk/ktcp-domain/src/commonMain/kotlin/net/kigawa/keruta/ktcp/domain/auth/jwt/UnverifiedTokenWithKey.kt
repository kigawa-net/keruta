package net.kigawa.keruta.ktcp.domain.auth.jwt

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface UnverifiedTokenWithKey {
    val unverifiedToken: UnverifiedToken
    fun verify(verifyValues: JwtVerifyValues): Res<VerifiedToken, KtcpErr>
}
