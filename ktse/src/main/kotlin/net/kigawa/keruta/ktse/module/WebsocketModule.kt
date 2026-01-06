package net.kigawa.keruta.ktse.module

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.ktcp.model.err.types.EntrypointNotFoundErr
import net.kigawa.keruta.ktcp.model.err.types.KtcpErr
import net.kigawa.keruta.ktcp.model.serialize.JsonMsgSerializer
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.KtcpSession
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktse.Config
import net.kigawa.keruta.ktse.ReceiveUnknownArg
import net.kigawa.keruta.ktse.WebsocketConnection
import net.kigawa.keruta.ktse.auth.Auth0JwtVerifier
import net.kigawa.keruta.ktse.err.SendGenericErrArg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.Duration.Companion.seconds

class WebsocketModule(application: Application) {
    val config = Config(application.environment)
    val jwtVerifier = Auth0JwtVerifier(config)
    val jsonSerializer = JsonMsgSerializer()
    val logger = getKogger()
    val ktcpServer = KtcpServer()

    init {
        application.install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }

    fun websocketModule(routing: Route) = routing.webSocket("/ws/ktcp") {
        KtcpSession.startSession(WebsocketConnection(this@webSocket)) { session ->
            incoming.consumeEach { frame ->
                session.updateTimeout()
                when (val res = receive(frame, ServerCtx(session, jsonSerializer, jwtVerifier))) {
                    is Res.Err<*, KtcpErr> -> {
                        ktcpServer.clientEntrypoints.genericError.access(
                            SendGenericErrArg(res.err), ServerCtx(session, jsonSerializer, jwtVerifier)
                        )
                    }
                    is Res.Ok<*, *> -> {
                    }
                }
            }
        }
    }

    suspend fun receive(frame: Frame, ctx: ServerCtx): Res<Unit, KtcpErr> = when (
        val res = ReceiveUnknownArg.fromFrame(frame, ctx)
    ) {
        is Res.Err<*, KtcpErr> -> {
            logger.error("Failed to decode frame", res.err)
            ctx.session.recordErr()
            res.convertType()
        }

        is Res.Ok<ReceiveUnknownArg, *> -> {
            ktcpServer.ktcpServerEntrypoints.access(res.value, ctx) ?: Res.Err(
                EntrypointNotFoundErr("entrypoint not found:")
            )
        }
    }
}
