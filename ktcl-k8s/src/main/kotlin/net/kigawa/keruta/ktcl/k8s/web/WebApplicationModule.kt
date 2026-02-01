package net.kigawa.keruta.ktcl.k8s.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import net.kigawa.keruta.ktcl.k8s.auth.AuthModule
import net.kigawa.keruta.ktcl.k8s.k8s.K8sModule
import net.kigawa.keruta.ktcl.k8s.route.RouteModule
import net.kigawa.keruta.ktcl.k8s.serialize.SerializeModule
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.getKogger

class WebApplicationModule() {
    val logger = getKogger()
    val k8sModule = K8sModule()
    val serializeModule = SerializeModule()
    val authModule = AuthModule()
    val routeModule = RouteModule()

    fun configure(application: Application) {
        logger.info("Starting ktcl-k8s Web Module")

        k8sModule.configure(application)
        serializeModule.configure(application)
        configureCors(application)
        authModule.configure(application)
        configureErrorHandling(application)
        routeModule.configure(application)

        logger.info("ktcl-k8s Web Module started successfully")
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
