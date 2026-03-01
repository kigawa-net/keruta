package net.kigawa.keruta.ktse.e2e

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * KTSE WebSocket e2e接続テスト
 */
class KtseWebSocketConnectionTest: KtseE2eTestBase() {

    @Test
    fun `WebSocketサーバーに接続できる`() = runBlocking {
        val client = KtcpWebSocketClient()
        client.connect(getWsUrl())
        client.close()
    }

    @Test
    fun `不正なURLには接続できない`() = runBlocking {
        val client = KtcpWebSocketClient()
        assertThrows<Exception> {
            client.connect("ws://localhost:9999/ws/ktcp")
        }
    }
}
