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
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.k8s.Main
import net.kigawa.keruta.ktcl.k8s.config.APP_CONFIG_KEY
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.configureAuth
import net.kigawa.keruta.ktcl.k8s.web.routes.configureConfigRoutes
import net.kigawa.keruta.ktcl.k8s.web.routes.configureStaticRoutes
import net.kigawa.kodel.api.log.LoggerFactory

class WebApplicationModule {
    private val logger = LoggerFactory.get("WebApplication")



    fun configure(application: Application) {
        logger.info("Starting ktcl-k8s Web Module")

        loadAppConfig(application)
        startK8sClient(application)
        configureJson(application)
        configureCors(application)
        configureSessions(application)
        configureErrorHandling(application)
        configureAuthentication(application)
        configureRouting(application)

        logger.info("ktcl-k8s Web Module started successfully")
    }

    private fun loadAppConfig(application: Application) {
        val appConfig = AppConfig.load(application.environment.config)
        application.attributes.put(APP_CONFIG_KEY, appConfig)
        logger.info("AppConfig loaded: server.port=${appConfig.server.port}, idp.issuer=${appConfig.idp.issuer}")
    }

    private fun startK8sClient(application: Application) {
        application.launch {
            logger.info("Starting K8s client in background")
            Main.client.start()
        }
    }

    private fun configureJson(application: Application) {
        application.install(ContentNegotiation) {
            json()
        }
    }

    private fun configureCors(application: Application) {
        application.install(CORS) {
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
    }

    private fun configureSessions(application: Application) {
        application.install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 3600
            }
        }
    }

    private fun configureErrorHandling(application: Application) {
        application.install(StatusPages) {
            exception<Throwable> { call, cause ->
                val errorLogger = LoggerFactory.get("ErrorHandler")
                errorLogger.severe("Unhandled error: ${cause.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Internal Server Error", cause.message ?: "Unknown error")
                )
            }
        }
    }

    private fun configureAuthentication(application: Application) {
        application.configureAuth()
    }

    private fun configureRouting(application: Application) {
        application.routing {
            configureConfigRoutes()
            configureStaticRoutes()
        }
    }
}
