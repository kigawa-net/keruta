package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.auth.PkceGenerator
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.web.login.LoginRoute
import net.kigawa.keruta.ktcl.k8s.web.routes.ConfigRoutes
import net.kigawa.keruta.ktcl.k8s.web.routes.StaticRoutes

class RouteModule {
    val oidcDiscoveryFetcher = OidcDiscoveryFetcher()
    val auth = AuthConfig(oidcDiscoveryFetcher)
    val pkceGenerator = PkceGenerator()
    val staticRoutes = StaticRoutes()

    fun configure(application: Application) {
        val appConfig = AppConfig.load(application.environment.config)
        val idpConfig = appConfig.idp
        val keycloakConfig = auth.loadKeycloakConfig(idpConfig.issuer, idpConfig.clientId)
        val jwkProvider = auth.createJwkProvider(keycloakConfig.jwksUrl)
        val configRoutes = ConfigRoutes(jwkProvider, keycloakConfig, appConfig)
        val loginRoute = LoginRoute(oidcDiscoveryFetcher, pkceGenerator, idpConfig)
        application.routing {
            configRoutes.configureConfigRoutes(this)
            staticRoutes.configure(this)
            loginRoute.configure(this)
        }
    }
}
