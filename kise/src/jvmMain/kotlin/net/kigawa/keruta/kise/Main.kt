package net.kigawa.keruta.kise

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import net.kigawa.keruta.kise.oidc.IdTokenVerifier
import net.kigawa.keruta.kise.oidc.OidcDiscoveryFetcher
import net.kigawa.keruta.kise.oidc.PkceGenerator
import net.kigawa.keruta.kise.oidc.model.OidcSession
import net.kigawa.keruta.kise.route.CallbackRoute
import net.kigawa.keruta.kise.route.LoginRoute
import net.kigawa.keruta.kise.websocket.KiseWebSocketServer
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = KiseConfig(environment)

    // セッション設定
    install(Sessions) {
        cookie<OidcSession>("OIDC_SESSION") {
            cookie.extensions["SameSite"] = "Lax"
        }
    }

    // ContentNegotiation設定
    install(ContentNegotiation) {
        json()
    }

    // WebSocket設定
    install(WebSockets.Plugin) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // OIDCコンポーネントの初期化
    val oidcDiscoveryFetcher = OidcDiscoveryFetcher()
    val pkceGenerator = PkceGenerator()
    val idTokenVerifier = IdTokenVerifier()
    val loginRoute = LoginRoute(oidcDiscoveryFetcher, pkceGenerator, config)
    val callbackRoute = CallbackRoute(oidcDiscoveryFetcher, idTokenVerifier, config)
    val webSocketServer = KiseWebSocketServer(this, config)

    // ルーティング設定
    routing {
        // OIDCログインルート
        loginRoute.configure(this)

        // OIDCコールバックルート
        callbackRoute.configure(this)

        // WebSocketエンドポイント
        webSocketServer.websocketModule(this)
    }
}
