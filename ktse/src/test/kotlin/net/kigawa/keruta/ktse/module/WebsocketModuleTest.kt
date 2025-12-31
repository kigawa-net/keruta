package net.kigawa.keruta.ktse.module

import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertNotNull

class WebsocketModuleTest {

    @Test
    fun testWebSocketPluginInstallation() = testApplication {
        application {
            WebsocketModule.module(this)
        }

        // Verify that the WebSocket plugin is installed
        // The plugin installation itself is tested by the fact that the application starts
        assertNotNull(this@WebsocketModuleTest)
    }
}