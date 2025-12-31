package net.kigawa.keruta.ktse

import kotlin.test.Test
import kotlin.test.assertTrue

class KerutaTaskServerTest {

    @Test
    fun testKerutaTaskServerObjectExists() {
        // Test that the KerutaTaskServer object exists
        assertTrue(KerutaTaskServer.ktcpServer != null)
    }

    @Test
    fun testLoggerExists() {
        // Test that the logger exists
        assertTrue(KerutaTaskServer.logger != null)
    }
}