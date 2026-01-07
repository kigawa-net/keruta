package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestMsg
import net.kigawa.keruta.ktcp.model.serialize.MsgSerializer
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.Verified
import net.kigawa.keruta.ktcp.model.err.types.VerifyErr
import net.kigawa.kodel.api.err.Res

class ServerCtx(
    val session: KtcpSession,
    val serializer: MsgSerializer,
    val jwtVerifier: JwtVerifier,
) {
    fun verify(authRequestMsg: AuthRequestMsg): Res<Verified, VerifyErr > {
        return jwtVerifier.verify(authRequestMsg.token)
    }

    val connection by session::connection
}
