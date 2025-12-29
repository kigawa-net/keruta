package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.ktcp.model.serialize.JsonMsgSerializer
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.KtcpSession
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getLogger
import net.kigawa.kodel.api.log.traceignore.error
import kotlin.time.Duration.Companion.seconds

object KerutaTaskServer {
    val ktcpServer = KtcpServer()
    val logger = getLogger()
    fun Application.module() {
        install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            websocketModule()
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
