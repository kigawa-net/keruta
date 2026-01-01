package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.ktcp.model.serialize.JsonMsgSerializer
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.KtcpSession
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktse.module.JwtModule
import net.kigawa.keruta.ktse.module.WebsocketModule
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.error

object KerutaTaskServer {
    val ktcpServer = KtcpServer()
    val logger = getKogger()
    fun Application.module() {
        WebsocketModule.module(this@module)
        JwtModule.module(this@module)
        routing {
            authenticate("keycloak") {
                websocketModule()
            }
        }
    }

    fun Routing.websocketModule() = webSocket("/ws/ktcp") {
        KtcpSession.startSession(WebsocketConnection(this@webSocket)) { session ->
            val jsonSerializer = JsonMsgSerializer()
            incoming.consumeEach { frame ->
                receive(frame, ServerCtx(session, jsonSerializer))
            }
        }
    }

    suspend fun receive(frame: Frame, ctx: ServerCtx) = when (
        val msg = ReceiveUnknownArg.fromFrame(frame, ctx)
    ) {
        is Res.Err<*, *> -> {
            logger.error("Failed to decode frame", msg.err)
            ctx.session.recordErr()
        }

        is Res.Ok<ReceiveUnknownArg, *> -> {
            ktcpServer.ktcpServerEntrypoints.access(msg.value, ctx)
        }
    }
}
