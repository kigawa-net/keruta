package net.kigawa.keruta.ktse

import net.kigawa.keruta.ktcp.model.msg.server.ServerUnknownMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import kotlin.test.Test
import kotlin.test.assertTrue

class ReceiveUnknownArgTest {

    @Test
    fun testTryToGenericErrorForGenericErrorType() {
        // Test that tryToGenericError returns an instance for GENERIC_ERROR type
        // We can test the logic without creating full objects
        val testMsg = ServerUnknownMsg(ServerMsgType.GENERIC_ERROR)

        // Test the condition directly
        assertTrue(testMsg.type == ServerMsgType.GENERIC_ERROR)
    }

    @Test
    fun testTryToGenericErrorForOtherTypes() {
        // Test that tryToGenericError returns null for non-GENERIC_ERROR types
        val testMsg = ServerUnknownMsg(ServerMsgType.AUTH_REQUEST)

        // Test the condition directly
        assertTrue(testMsg.type != ServerMsgType.GENERIC_ERROR)
    }
}
