package net.kigawa.keruta.ktse.e2e

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import net.kigawa.keruta.ktse.KerutaTaskServer
import net.kigawa.keruta.ktse.websocket.KtorWebsocketModule

/**
 * KTSE e2eテスト用基底クラス（Docker不要バージョン）
 * モックを使用して認証をスキップ
 */
abstract class KtseE2eTestBase {

    companion object {
        var server: EmbeddedServer<*, *>? = null
        const val SERVER_PORT = 18080

        fun startServer() {
            // 環境変数設定
            System.setProperty("DB_JDBC_URL", "jdbc:mysql://localhost:3306/keruta_test")
            System.setProperty("DB_USERNAME", "keruta")
            System.setProperty("DB_PASSWORD", "keruta")
            System.setProperty("ZK_HOST", "localhost:2181")
            System.setProperty("IDP_0_ISSUER", "https://user.kigawa.net/realms/develop")
            System.setProperty("IDP_0_AUDIENCE", "keruta")
            System.setProperty("PROVIDER_0_ISSUER", "https://user.kigawa.net/realms/develop")
            System.setProperty("PROVIDER_0_AUDIENCE", "keruta")
            System.setProperty("PROVIDER_0_NAME", "keruta-provider")
            System.setProperty("KTSE_JWT_SECRET", "test-jwt-secret-key-for-e2e-testing-only")

            // KTSEサーバーを起動
            server = embeddedServer(Netty, port = SERVER_PORT) {
                install(WebSockets.Plugin)

                val kerutaTaskServer = KerutaTaskServer()

                val ws = KtorWebsocketModule(this, kerutaTaskServer)
                routing {
                    ws.websocketModule(this)
                }
            }.start(wait = false)

            // サーバー起動待機
            Thread.sleep(3000)
        }

        fun stopServer() {
            server?.stop(1000, 2000)
            server = null
        }

        @JvmStatic
        fun getWsUrl(): String = "ws://localhost:$SERVER_PORT/ws/ktcp"
    }
}
