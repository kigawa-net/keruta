package net.kigawa.keruta.ktcl.web

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.module.JwtModule
import net.kigawa.keruta.ktcl.web.module.WebsocketModule
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.kodel.api.log.getKogger

object KerutaTaskClientWeb {
    val ktcpClient = KtcpClient()
    val logger = getKogger()
    fun Application.module() {
        WebsocketModule.module(this@module)
        JwtModule.module(this@module)
        routing {
            get("/") {
                call.respondText("Welcome to Keruta Task Client Web")
            }
            authenticate("auth-jwt") {

            }
        }
    }
}
