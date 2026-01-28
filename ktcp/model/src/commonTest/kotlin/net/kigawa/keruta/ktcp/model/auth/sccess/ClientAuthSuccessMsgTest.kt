package net.kigawa.keruta.ktcp.model.auth.sccess

import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ClientAuthSuccessMsgTest {
    @Test
    fun testCreateClientAuthSuccessMsg() {
        // Arrange & Act
        val msg = ClientAuthSuccessMsg(
            type = ServerMsgType.AUTH_SUCCESS
        )

        // Assert
        assertEquals(ServerMsgType.AUTH_SUCCESS, msg.type)
    }

    @Test
    fun testCreateClientAuthSuccessMsgWithDefaultType() {
        // Arrange & Act
        val msg = ClientAuthSuccessMsg()

        // Assert
        assertEquals(ServerMsgType.AUTH_SUCCESS, msg.type)
    }

    @Test
    fun testCreateClientAuthSuccessMsgWithInvalidTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            ClientAuthSuccessMsg(
                type = ServerMsgType.TASK_CREATE
            )
        }
    }

    @Test
    fun testCreateClientAuthSuccessMsgWithGenericErrorTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            ClientAuthSuccessMsg(
                type = ServerMsgType.GENERIC_ERROR
            )
        }
    }
}
