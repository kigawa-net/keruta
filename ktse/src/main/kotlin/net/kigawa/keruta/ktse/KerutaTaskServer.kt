package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktse.websocket.KtorWebsocketModule
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.handler.StdHandler

@Suppress("unused")
class KerutaTaskServer {

    init {
        LoggerFactory.configure {
            level = LogLevel.INFO
            handler(::StdHandler) {
                level = LogLevel.DEBUG
            }

            child("net.kigawa") {
                child("keruta") {
                    level = LogLevel.DEBUG
                }
                child("kodel") {
//                        level = LogLevel.DEBUG
                }
            }
        }
    }


    @Suppress("unused")
    fun Application.module() {
        val ws = KtorWebsocketModule(this@module,this@KerutaTaskServer)
        routing {
            ws.websocketModule(this@routing)
        }
    }
}
