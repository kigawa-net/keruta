package net.kigawa.keruta.ktcp.model.auth.oidc

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.kodel.api.err.Res

interface UnverifiedTokenWithOidc {
    fun useJwks(): Res<UnverifiedTokenWithKey, KtcpErr>
}
