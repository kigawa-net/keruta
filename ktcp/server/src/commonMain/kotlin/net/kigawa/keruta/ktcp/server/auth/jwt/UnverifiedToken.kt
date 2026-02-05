package net.kigawa.keruta.ktcp.server.auth.jwt

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.kodel.api.err.Res

interface UnverifiedToken {
    suspend fun withOidcConfig(): Res<UnverifiedTokenWithOidc, KtcpErr>

    val subject: String
    val issuer: String
}
