package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.KerutaEndpoints
import net.kigawa.keruta.ktcl.k8s.auth.PkceGenerator
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.web.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.web.login.LoginCallbackRoute
import net.kigawa.keruta.ktcl.k8s.web.login.LoginRoute
import net.kigawa.keruta.ktcl.k8s.web.login.TokenRoute
import net.kigawa.keruta.ktcl.k8s.web.routes.ConfigRoutes
import net.kigawa.keruta.ktcl.k8s.web.routes.StaticRoutes

class RouteModule {
    private val oidcDiscoveryFetcher = OidcDiscoveryFetcher()
    private val auth = AuthConfig(oidcDiscoveryFetcher)
    private val pkceGenerator = PkceGenerator()
    private val loginCallbackRoute = LoginCallbackRoute()

    fun configure(application: Application) {
        val appConfig = AppConfig.load(application.environment.config)
        val idpConfig = appConfig.idp
        val keycloakConfig = auth.loadKeycloakConfig(idpConfig.issuer, idpConfig.clientId)
        val jwkProvider = auth.createJwkProvider(keycloakConfig.jwksUrl)
        
        // 認証ヘルパーと静的ルートを初期化
        val authenticationHelper = AuthenticationHelper(jwkProvider, keycloakConfig)
        val staticRoutes = StaticRoutes(authenticationHelper)
        
        val configRoutes = ConfigRoutes(jwkProvider, keycloakConfig, appConfig)
        val kerutaEndpoints = KerutaEndpoints(appConfig.keruta)
        val loginRoute = LoginRoute(oidcDiscoveryFetcher, pkceGenerator, idpConfig, kerutaEndpoints)
        val tokenRoute = TokenRoute(oidcDiscoveryFetcher, idpConfig)
        
        application.routing {
            configRoutes.configureConfigRoutes(this)
            staticRoutes.configure(this)
            loginRoute.configure(this)
            tokenRoute.configure(this)
            loginCallbackRoute.configure(this)
        }
    }
}
