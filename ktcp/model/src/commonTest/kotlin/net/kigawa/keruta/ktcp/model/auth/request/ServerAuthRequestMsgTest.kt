package net.kigawa.keruta.ktcp.model.auth.request

import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerAuthRequestMsgTest {
    @Test
    fun testCreateServerAuthRequestMsg() {
        // Arrange & Act
        val msg = ServerAuthRequestMsg(
            userToken = "user-token-123",
            type = ServerMsgType.AUTH_REQUEST,
            serverToken = "server-token-456"
        )

        // Assert
        assertEquals(ServerMsgType.AUTH_REQUEST, msg.type)
        assertEquals("user-token-123", msg.userToken)
        assertEquals("server-token-456", msg.serverToken)
    }

    @Test
    fun testCreateServerAuthRequestMsgWithDefaultType() {
        // Arrange & Act
        val msg = ServerAuthRequestMsg(
            userToken = "user-token-123",
            serverToken = "server-token-456"
        )

        // Assert
        assertEquals(ServerMsgType.AUTH_REQUEST, msg.type)
    }

    @Test
    fun testCreateServerAuthRequestMsgWithInvalidTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            ServerAuthRequestMsg(
                userToken = "user-token-123",
                type = ServerMsgType.TASK_CREATE,
                serverToken = "server-token-456"
            )
        }
    }

    @Test
    fun testCreateServerAuthRequestMsgWithGenericErrorTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            ServerAuthRequestMsg(
                userToken = "user-token-123",
                type = ServerMsgType.GENERIC_ERROR,
                serverToken = "server-token-456"
            )
        }
    }
}
