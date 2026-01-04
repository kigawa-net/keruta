package net.kigawa.keruta.ktcl.web

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.web.module.JwtModule
import net.kigawa.keruta.ktcl.web.module.WebsocketModule
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.handler.StdHandler
import net.kigawa.kodel.api.log.traceignore.debug

@Serializable
data class UserSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)

@Suppress("unused")
class KerutaTaskClientWeb(val application: Application) {
    val ktcpClient = KtcpClient()
    val logger = getKogger()
    val config = Config.load(application.environment.config)
    val webEntrypoints = WebEntrypoints(config)
    fun module() = application.apply {
        WebsocketModule.module(application)
        JwtModule.module(application, config)
        install(Sessions) {
            cookie<UserSession>("USER_SESSION", SessionStorageMemory())
        }
        install(CORS) {
            allowMethod(io.ktor.http.HttpMethod.Options)
            allowMethod(io.ktor.http.HttpMethod.Get)
            allowMethod(io.ktor.http.HttpMethod.Post)
            allowMethod(io.ktor.http.HttpMethod.Put)
            allowMethod(io.ktor.http.HttpMethod.Delete)
            allowHeader(io.ktor.http.HttpHeaders.Authorization)
            allowHeader(io.ktor.http.HttpHeaders.ContentType)
            allowHeader(io.ktor.http.HttpHeaders.Upgrade)
            allowHeader(io.ktor.http.HttpHeaders.Connection)
            allowHeader("Sec-WebSocket-Key")
            allowHeader("Sec-WebSocket-Version")
            allowHeader("Sec-WebSocket-Protocol")
            allowCredentials = true
            anyHost() // 開発用 - 本番では特定のホストを指定
        }
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
                    child("keruta.ktcl.web") {
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
            KerutaTaskClientWeb(this).module()
        }
    }
}
