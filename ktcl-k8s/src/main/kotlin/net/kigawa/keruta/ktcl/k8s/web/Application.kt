package net.kigawa.keruta.ktcl.k8s.web

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.k8s.web.auth.configureAuth
import net.kigawa.keruta.ktcl.k8s.web.routes.configureConfigRoutes
import net.kigawa.keruta.ktcl.k8s.web.routes.configureStaticRoutes
import net.kigawa.kodel.api.log.Kogger
import net.kigawa.kodel.api.log.LoggerFactory

fun Application.module() {
    val logger = LoggerFactory.get("WebApplication")
    logger.info("Starting ktcl-k8s Web Module")

    // JSON設定
    install(ContentNegotiation) {
        json()
    }

    // CORS設定
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
        anyHost() // 本番環境では特定のホストのみ許可すること
    }

    // セッション設定
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600
        }
    }

    // エラーハンドリング
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val errorLogger = LoggerFactory.get("ErrorHandler")
            errorLogger.severe("Unhandled error: ${cause.message}")
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal Server Error", cause.message ?: "Unknown error")
            )
        }
    }

    // 認証設定
    configureAuth()

    // ルーティング設定
    routing {
        configureConfigRoutes()
        configureStaticRoutes()
    }

    logger.info("ktcl-k8s Web Module started successfully")
}

@Serializable
data class UserSession(
    val userId: String,
    val token: String
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)
