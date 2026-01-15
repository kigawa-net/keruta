package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface UnverifiedToken {
    suspend fun verify(idp: Idp): Res<VerifiedToken, KtcpErr>

    val subject: String
    val issuer: String
}
