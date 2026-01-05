package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateMsg
import net.kigawa.keruta.ktcp.model.serialize.MsgSerializer
import net.kigawa.keruta.ktcp.server.authenticate.JwtVerifier
import net.kigawa.keruta.ktcp.server.authenticate.Verified
import net.kigawa.keruta.ktcp.model.err.types.VerifyErr
import net.kigawa.kodel.api.err.Res

class ServerCtx(
    val session: KtcpSession,
    val serializer: MsgSerializer,
    val jwtVerifier: JwtVerifier,
) {
    fun verify(authenticateMsg: AuthenticateMsg): Res<Verified, VerifyErr > {
        return jwtVerifier.verify(authenticateMsg.token)
    }

    val connection by session::connection
}
