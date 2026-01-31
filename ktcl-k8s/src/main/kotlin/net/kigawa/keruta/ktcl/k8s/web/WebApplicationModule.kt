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
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.k8s.KerutaK8sClient
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthConfig
import net.kigawa.keruta.ktcl.k8s.web.routes.ConfigRoutes
import net.kigawa.keruta.ktcl.k8s.web.routes.configureStaticRoutes
import net.kigawa.kodel.api.log.LoggerFactory

class WebApplicationModule(val application: Application) {
    private val logger = LoggerFactory.get("WebApplication")
    val auth = AuthConfig()
    val appConfig = AppConfig.load(application.environment.config)
    val idpConfig = appConfig.idp
    val keycloakConfig = auth.loadKeycloakConfig(idpConfig.issuer, idpConfig.audience)
    val jwkProvider = auth.createJwkProvider(keycloakConfig.jwksUrl)
    val config = K8sConfig.fromEnvironment()
    val client = KerutaK8sClient(config)


    fun configure() {
        logger.info("Starting ktcl-k8s Web Module")

        startK8sClient()
        configureJson()
        configureCors()
        configureSessions()
        configureErrorHandling()
        logger.info("Configuring Keycloak authentication: ${keycloakConfig.issuer}")
        configureRouting()

        logger.info("ktcl-k8s Web Module started successfully")
    }

    private fun startK8sClient() {
        application.launch {
            logger.info("Starting K8s client in background")
            client.start()
        }
    }

    private fun configureJson() {
        application.install(ContentNegotiation) {
            json()
        }
    }

    private fun configureCors() {
        application.install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowCredentials = true
        }
    }

    private fun configureSessions() {
        application.install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 3600
            }
        }
    }

    private fun configureErrorHandling() {
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

    private fun configureRouting() {
        application.routing {
            ConfigRoutes(jwkProvider, keycloakConfig, appConfig).configureConfigRoutes(this)
            configureStaticRoutes()
        }
    }
}
