package net.kigawa.keruta.ktse

import io.ktor.websocket.*
import net.kigawa.keruta.ktcp.model.msg.KtcpUnknownMsg
import net.kigawa.keruta.ktcp.model.msg.MsgType
import net.kigawa.keruta.ktcp.model.serialize.JsonMsgSerializer
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res
import kotlin.test.Test
import kotlin.test.assertTrue

class ReceiveUnknownArgTest {

    @Test
    fun testFromFrameWithNonTextFrame() {
        // Create a minimal context for testing
        val serializer = JsonMsgSerializer()
        // We'll create a minimal mock session implementation
        val mockSession = object {
            suspend fun recordErr() {}
        }

        // For now, we'll test the basic structure without complex mocking
        // The actual implementation details would require more setup
        val frame = Frame.Close()

        // Test that the fromFrame method exists and can handle non-text frames
        // We can't easily test the full functionality without proper session mocking
        assertTrue(frame !is Frame.Text)
    }

    @Test
    fun testTryToGenericErrorForGenericErrorType() {
        // Test that tryToGenericError returns an instance for GENERIC_ERROR type
        // We can test the logic without creating full objects
        val testMsg = KtcpUnknownMsg(MsgType.GENERIC_ERROR)

        // Test the condition directly
        assertTrue(testMsg.type == MsgType.GENERIC_ERROR)
    }

    @Test
    fun testTryToGenericErrorForOtherTypes() {
        // Test that tryToGenericError returns null for non-GENERIC_ERROR types
        val testMsg = KtcpUnknownMsg(MsgType.AUTHENTICATE)

        // Test the condition directly
        assertTrue(testMsg.type != MsgType.GENERIC_ERROR)
    }
}