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
        val user = when (val user = jwtVerifier.verifyUserToken(
            authRequestMsg.userToken
        )) {
            is Res.Err -> return user.convertType()
            is Res.Ok -> user.value
        }
        val server =when(val server = jwtVerifier.verifyServerToken(
            token = authRequestMsg.serverToken,
            subject = user.sub
        )) {
            is Res.Err -> return server.convertType()
            is Res.Ok -> server.value
        }
        return Res.Ok(Verified(user, server))
    }

    val connection by session::connection
    val persisterSession by session::persisterSession
}
