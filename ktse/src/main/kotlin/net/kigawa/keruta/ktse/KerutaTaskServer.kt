package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import net.kigawa.keruta.ktcp.model.msg.UnknownArg
import net.kigawa.keruta.ktcp.server.KtcpServer
import net.kigawa.keruta.ktcp.server.KtcpSession
import net.kigawa.keruta.ktse.reader.FrameDecodeErr
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
        KtcpSession.startSession(WebsocketConnection(this@webSocket)) { con ->
            incoming.consumeEach { frame ->
                receive(frame, con)
            }
        }
    }

    fun receive(frame: Frame, con: KtcpSession) = when (
        val msg = FrameReader.readToMsg(frame)
    ) {
        is Res.Err<UnknownArg, FrameDecodeErr> -> {
            logger.error("Failed to decode frame", msg.err)
            con.recordErr()
        }

        is Res.Ok<UnknownArg, FrameDecodeErr> -> Unit
    }
}
