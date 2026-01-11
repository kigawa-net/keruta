package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.Verified
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.session.KtcpSession
import net.kigawa.kodel.api.err.Res

class ServerCtx(
    val session: KtcpSession,
    val serializer: KerutaSerializer,
    val jwtVerifier: JwtVerifier,
    val server: KtcpServer,
) {
    fun verify(authRequestMsg: ServerAuthRequestMsg): Res<Verified, VerifyErr> {
        return jwtVerifier.verify(authRequestMsg.token)
    }

    val connection by session::connection
    val persisterSession by session::persisterSession
}
