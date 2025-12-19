package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcp.server.KtcpServer

object KerutaTaskServer {
    val serverEntrypoints = KtcpServer()
    fun Application.module() {
        routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
}
