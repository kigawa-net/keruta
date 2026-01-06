package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktse.module.WebsocketModule
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.handler.StdHandler

object KerutaTaskServer {
    val logger = getKogger()
    init {
        LoggerFactory.configure {
            level = LogLevel.INFO
            handler(::StdHandler) {
                level = LogLevel.DEBUG
            }
            child("net.kigawa") {
                child("keruta.ktse") {
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
        val ws = WebsocketModule(this@module)
        routing {
            ws.websocketModule(this@routing)
        }
    }

}
