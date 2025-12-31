package net.kigawa.keruta.ktse

import kotlin.test.Test
import kotlin.test.assertTrue

class KerutaTaskServerIntegrationTest {

    @Test
    fun testKtcpServerExists() {
        // Test that the ktcpServer exists
        assertTrue(KerutaTaskServer.ktcpServer != null)
    }

    @Test
    fun testIntegrationComponentsExist() {
        // Test that all main integration components exist
        assertTrue(KerutaTaskServer.ktcpServer != null)
        assertTrue(KerutaTaskServer.logger != null)
    }
}