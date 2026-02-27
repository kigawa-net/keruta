package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface UnverifiedTokenWithKey {
    fun verify(verifyValues: JwtVerifyValues): Res<VerifiedToken, KtcpErr>
}
