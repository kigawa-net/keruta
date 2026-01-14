package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.server.session.AuthenticatedSession
import net.kigawa.keruta.ktcp.server.session.KtcpSession
import net.kigawa.kodel.api.err.Res

class ServerCtx(
    val session: KtcpSession,
    val serializer: KerutaSerializer,
    val server: KtcpServer,
) {
    suspend fun verify(authRequestMsg: ServerAuthRequestMsg): Res<AuthenticatedSession, KtcpErr> {
        val persisterSession = when (
            val res = persisterSession.verify(
                authRequestMsg
            )
        ) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        return Res.Ok(AuthenticatedSession(session, persisterSession))
    }

    val connection by session::connection
    val persisterSession by session::persisterSession
}
