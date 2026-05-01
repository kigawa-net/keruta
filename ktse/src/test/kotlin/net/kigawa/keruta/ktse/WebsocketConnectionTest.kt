package net.kigawa.keruta.ktse

import kotlin.test.Test
import kotlin.test.assertTrue

class WebsocketConnectionTest {

    @Test
    fun testWebsocketConnectionImplementsKtcpConnection() {
        // We can't easily instantiate WebsocketConnection without a session,
        // but we can verify the class implements the interface by checking its interfaces
        val interfaces = WebsocketConnection::class.java.interfaces
        assertTrue(interfaces.any { it.name.contains("KtcpConnection") })
    }
}
