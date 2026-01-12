package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.VerifyConfig
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.session.KtcpSession
import net.kigawa.kodel.api.err.Res

class ServerCtx(
    val session: KtcpSession,
    val serializer: KerutaSerializer,
    val jwtVerifier: JwtVerifier,
    val server: KtcpServer,
    val userVerifyConfig: VerifyConfig,
) {
    fun verify(authRequestMsg: ServerAuthRequestMsg): Res<VerifiedToken, VerifyErr> {
        val user = when(val user = jwtVerifier.verify(
            token = authRequestMsg.userToken,
            verifyConfig = userVerifyConfig
        )) {
            is Res.Err -> return user
            is Res.Ok -> user.value
        }
        jwtVerifier.verify(
            token = authRequestMsg.serverToken,
            verifyConfig = object :VerifyConfig {
                override val issuer: String
                    get() = userVerifyConfig.issuer
                override val jwksUrl: String?
                    get() = TODO("Not yet implemented")
                override val audience: String
                    get() = TODO("Not yet implemented")
            })
    }

    val connection by session::connection
    val persisterSession by session::persisterSession
}
