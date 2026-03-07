package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.KerutaEndpoints
import net.kigawa.keruta.ktcl.k8s.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.auth.PkceGenerator
import net.kigawa.keruta.ktcl.k8s.auth.RemoteConfigProvider
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.login.LoginCallbackRoute
import net.kigawa.keruta.ktcl.k8s.login.LoginRoute
import net.kigawa.keruta.ktcl.k8s.login.ProviderRegistrationClient
import net.kigawa.keruta.ktcl.k8s.login.TokenRoute
import net.kigawa.keruta.ktcl.k8s.persist.DbModule
import net.kigawa.keruta.ktcp.base.auth.jwks.JwksProvider
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.base.auth.key.Auth0AlgorithmInitializer
import net.kigawa.keruta.ktcp.base.auth.key.JavaPrivateKeyInitializer
import net.kigawa.keruta.ktcp.base.auth.oidc.OidcConfigProvider
import net.kigawa.keruta.ktcp.base.http.HttpClient

class RouteModule(
    private val httpClient: HttpClient,
    private val dbModule: DbModule,
    private val oidcDiscoveryFetcher: OidcDiscoveryFetcher,
) {
    private val remoteConfigProvider = RemoteConfigProvider(oidcDiscoveryFetcher)
    private val pkceGenerator = PkceGenerator()
    private val userTokenDao = dbModule.userTokenDao
    private val javaPrivateKeyInitializer = JavaPrivateKeyInitializer()
    private val auth0AlgorithmInitializer = Auth0AlgorithmInitializer()

    fun configure(application: Application, appConfig: AppConfig) {
        val providerRegistrationClient = ProviderRegistrationClient(
            ktseConfig = appConfig.ktse,
            privateKey = appConfig.auth.privateKey,
            issuer = appConfig.keruta.ownIssuer.toStrUrl(),
            javaPrivateKeyInitializer = javaPrivateKeyInitializer,
        )
        val loginCallbackRoute = LoginCallbackRoute(oidcDiscoveryFetcher, userTokenDao, providerRegistrationClient)
        val idpConfig = appConfig.idp
        val keycloakConfig = remoteConfigProvider.loadKeycloakConfig(idpConfig.issuer, idpConfig.clientId)
        val jwkProvider = remoteConfigProvider.createJwkProvider(keycloakConfig.jwksUrl)
        val oidcConfigProvider = OidcConfigProvider(httpClient)
        val jwksProvider = JwksProvider()
        val auth0JwtVerifier = Auth0JwtVerifier(
            oidcConfigProvider, jwksProvider, auth0AlgorithmInitializer, javaPrivateKeyInitializer
        )
        val authConfig = appConfig.auth
        // 認証ヘルパーと静的ルートを初期化
        val authenticationHelper = AuthenticationHelper(auth0JwtVerifier, authConfig.privateKey)
        val staticRoutes = StaticRoutes(authenticationHelper)
        val configRoutes = ConfigRoutes(
            jwkProvider, keycloakConfig, appConfig, auth0JwtVerifier,
            authConfig.privateKey, dbModule.userClaudeConfigDao, javaPrivateKeyInitializer
        )
        val kerutaEndpoints = KerutaEndpoints(appConfig.keruta)
        val loginRoute = LoginRoute(
            oidcDiscoveryFetcher, pkceGenerator, idpConfig, kerutaEndpoints
        )
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
