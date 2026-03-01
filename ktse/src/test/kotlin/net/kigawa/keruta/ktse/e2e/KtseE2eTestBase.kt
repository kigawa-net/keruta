package net.kigawa.keruta.ktse.e2e

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import net.kigawa.keruta.ktse.KerutaTaskServer
import net.kigawa.keruta.ktse.websocket.KtorWebsocketModule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.time.Duration.Companion.seconds

/**
 * KTSE e2eテスト用基底クラス
 * テスト用MySQLコンテナとWebSocketサーバーを起動
 */
@Testcontainers
abstract class KtseE2eTestBase {
    
    companion object {
        @Container
        val mysqlContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withDatabaseName("keruta_test")
            .withUsername("keruta")
            .withPassword("keruta")
            .waitingFor(Wait.forListeningPort())
        
        protected var server: EmbeddedServer<*, *>? = null
        protected const val SERVER_PORT = 18080
        
        @BeforeAll
        @JvmStatic
        fun startServer() {
            // 環境変数設定
            System.setProperty("DB_JDBC_URL", mysqlContainer.jdbcUrl)
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
                install(WebSockets.Plugin) {
                    pingPeriod = 15.seconds
                    timeout = 15.seconds
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
                
                val kerutaTaskServer = KerutaTaskServer()
                
                val ws = KtorWebsocketModule(this, kerutaTaskServer)
                routing {
                    ws.websocketModule(this)
                }
            }.start(wait = false)
            
            // サーバー起動待機
            Thread.sleep(3000)
        }
        
        @AfterAll
        @JvmStatic
        fun stopServer() {
            server?.stop(1000, 2000)
            server = null
        }
        
        protected fun getWsUrl(): String = "ws://localhost:$SERVER_PORT/ws/ktcp"
    }
}
