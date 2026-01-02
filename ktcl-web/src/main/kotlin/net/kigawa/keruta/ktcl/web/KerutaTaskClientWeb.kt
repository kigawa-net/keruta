package net.kigawa.keruta.ktcl.web

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.module.JwtModule
import net.kigawa.keruta.ktcl.web.module.WebsocketModule
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.handler.StdHandler
import net.kigawa.kodel.api.log.traceignore.debug
import java.util.logging.Logger


class KerutaTaskClientWeb(val application: Application) {
    val ktcpClient = KtcpClient()
    val logger = getKogger()
    val config = Config.load(application.environment.config)
    val webEntrypoints = WebEntrypoints(config)
    fun module() = application.apply {
        WebsocketModule.module(application)
        JwtModule.module(application, config)
        install(ContentNegotiation) {
            json()
        }
        logger.debug("Registering entrypoints")
        routing {
            webEntrypoints.flat().forEach { entrypoint ->
                logger.debug("Registering entrypoint: ${entrypoint.path}")
                route(entrypoint.path.joinToString(separator = "/", prefix = "/") { it.name.raw }) {
                    logger.debug("Registering entrypoint: ${entrypoint.path}")
                    entrypoint.access(this@route, Unit)
                }
            }
        }
    }

    companion object {
        init {
            LoggerFactory.configure {
                level = LogLevel.INFO
                handler(::StdHandler) {
                    level = LogLevel.DEBUG
                }
                child("net.kigawa") {
                    level = LogLevel.DEBUG
                }
            }
        }

        fun Application.module() {
            KerutaTaskClientWeb(this).module()
        }
    }
}
