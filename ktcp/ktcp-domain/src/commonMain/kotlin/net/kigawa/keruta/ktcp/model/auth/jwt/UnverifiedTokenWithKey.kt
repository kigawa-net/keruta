package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface UnverifiedTokenWithKey {
    val unverifiedToken: UnverifiedToken
    fun verify(verifyValues: JwtVerifyValues): Res<VerifiedToken, KtcpErr>
}
