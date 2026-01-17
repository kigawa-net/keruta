package net.kigawa.keruta.ktcp.model.msg

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerMsgTypeTest {
    @Test
    fun testFromStringGenericError() {
        // Arrange & Act
        val type = ServerMsgType.fromString("generic_error")

        // Assert
        assertEquals(ServerMsgType.GENERIC_ERROR, type)
    }

    @Test
    fun testFromStringAuthRequest() {
        // Arrange & Act
        val type = ServerMsgType.fromString("auth_request")

        // Assert
        assertEquals(ServerMsgType.AUTH_REQUEST, type)
    }

    @Test
    fun testFromStringAuthSuccess() {
        // Arrange & Act
        val type = ServerMsgType.fromString("auth_success")

        // Assert
        assertEquals(ServerMsgType.AUTH_SUCCESS, type)
    }

    @Test
    fun testFromStringTaskCreate() {
        // Arrange & Act
        val type = ServerMsgType.fromString("task_create")

        // Assert
        assertEquals(ServerMsgType.TASK_CREATE, type)
    }

    @Test
    fun testFromStringProvidersRequest() {
        // Arrange & Act
        val type = ServerMsgType.fromString("providers_request")

        // Assert
        assertEquals(ServerMsgType.PROVIDERS_REQUEST, type)
    }

    @Test
    fun testFromStringInvalidTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<NoSuchElementException> {
            ServerMsgType.fromString("invalid_type")
        }
    }

    @Test
    fun testFromStringEmptyStringFails() {
        // Arrange & Act & Assert
        assertFailsWith<NoSuchElementException> {
            ServerMsgType.fromString("")
        }
    }

    @Test
    fun testServerMsgTypeStringValues() {
        // Assert
        assertEquals("generic_error", ServerMsgType.GENERIC_ERROR.str)
        assertEquals("auth_request", ServerMsgType.AUTH_REQUEST.str)
        assertEquals("auth_success", ServerMsgType.AUTH_SUCCESS.str)
        assertEquals("task_create", ServerMsgType.TASK_CREATE.str)
        assertEquals("providers_request", ServerMsgType.PROVIDERS_REQUEST.str)
    }

    @Test
    fun testServerMsgTypeEntriesCount() {
        // Assert
        assertEquals(5, ServerMsgType.entries.size)
    }
}