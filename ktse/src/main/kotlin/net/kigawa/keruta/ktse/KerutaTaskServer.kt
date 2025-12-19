package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import net.kigawa.keruta.ktcp.server.KtcpServer
import kotlin.time.Duration.Companion.seconds

object KerutaTaskServer {
    val serverEntrypoints = KtcpServer()
    fun Application.module() {
        install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
        routing {
            webSocket("/ws/ktcp") {

            }
        }
    }
}
