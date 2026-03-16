package net.kigawa.keruta.ktcp.domain.auth.oidc

import net.kigawa.keruta.ktcp.domain.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface UnverifiedTokenWithOidc {
    fun useJwks(): Res<UnverifiedTokenWithKey, KtcpErr>
}
