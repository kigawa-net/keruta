package net.kigawa.keruta.ktcl.k8s.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import net.kigawa.keruta.ktcl.k8s.auth.AuthModule
import net.kigawa.keruta.ktcl.k8s.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.config.CorsConfig
import net.kigawa.keruta.ktcl.k8s.err.ErrorResponse
import net.kigawa.keruta.ktcl.k8s.k8s.K8sModule
import net.kigawa.keruta.ktcl.k8s.persist.DbModule
import net.kigawa.keruta.ktcl.k8s.route.RouteModule
import net.kigawa.keruta.ktcl.k8s.serialize.SerializeModule
import net.kigawa.keruta.ktcp.base.http.HttpClient
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.getKogger

class WebApplicationModule {
    val logger = getKogger()
    val k8sModule = K8sModule()
    val serializeModule = SerializeModule()
    val authModule = AuthModule()
    private val httpClient = HttpClient()
    private lateinit var dbModule: DbModule
    lateinit var routeModule: RouteModule

    fun configure(application: Application) {
        logger.info("Starting ktcl-k8s Web Module")
        val appConfig = AppConfig.load(application.environment.config)
        dbModule = DbModule.create(appConfig)
        val oidcDiscoveryFetcher = OidcDiscoveryFetcher()
        routeModule = RouteModule(httpClient, dbModule, oidcDiscoveryFetcher)

        k8sModule.configure(application, dbModule.userTokenDao, appConfig.idp, oidcDiscoveryFetcher)
        serializeModule.configure(application)
        configureCors(application)
        authModule.configure(application)
        configureErrorHandling(application)
        routeModule.configure(application, appConfig)

        logger.info("ktcl-k8s Web Module started successfully")
    }

    private fun configureCors(application: Application) {
        val corsConfig = CorsConfig.fromEnvironment()
        application.install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowCredentials = true
            val origins = corsConfig.allowedOrigins
            if (origins != null) {
                origins.forEach { origin ->
                    val scheme = when {
                        origin.startsWith("https://") -> "https"
                        origin.startsWith("http://") -> "http"
                        else -> "http"
                    }
                    val hostWithPort = origin.removePrefix("https://").removePrefix("http://")
                    allowHost(hostWithPort, schemes = listOf(scheme))
                }
            } else {
                anyHost()
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


}
