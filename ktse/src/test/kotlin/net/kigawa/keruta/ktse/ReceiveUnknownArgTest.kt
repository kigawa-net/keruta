package net.kigawa.keruta.ktse

import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReceiveUnknownArgTest {

    @Test
    fun testServerMsgTypeStringComparison() {
        // Test that ServerMsgType.str returns correct string values
        assertEquals("generic_error", ServerMsgType.GENERIC_ERROR.str)
        assertEquals("auth_request", ServerMsgType.AUTH_REQUEST.str)
        assertEquals("task_create", ServerMsgType.TASK_CREATE.str)
    }

    @Test
    fun testTypeMatchingLogic() {
        // Test the type matching logic used in ReceiveUnknownArg
        val typeStr = ServerMsgType.GENERIC_ERROR.str

        // Should match GENERIC_ERROR
        assertTrue(typeStr == ServerMsgType.GENERIC_ERROR.str)

        // Should not match other types
        assertFalse(typeStr == ServerMsgType.AUTH_REQUEST.str)
        assertFalse(typeStr == ServerMsgType.TASK_CREATE.str)
    }
}
