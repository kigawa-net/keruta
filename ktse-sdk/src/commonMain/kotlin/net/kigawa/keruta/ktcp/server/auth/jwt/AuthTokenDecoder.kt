package net.kigawa.keruta.ktcp.server.auth.jwt

import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.UnverifiedAuthTokens
import net.kigawa.kodel.api.err.Res

interface AuthTokenDecoder {
    fun decodeAuthRequestMsg(
        authRequestMsg: ServerAuthRequestMsg
    ): Res<UnverifiedAuthTokens, KtcpErr>
}
