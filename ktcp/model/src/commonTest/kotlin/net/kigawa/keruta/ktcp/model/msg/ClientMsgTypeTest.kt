package net.kigawa.keruta.ktcp.model.msg

import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ClientMsgTypeTest {
    @Test
    fun testFromStringGenericError() {
        // Arrange & Act
        val type = ClientMsgType.fromString("generic_error")

        // Assert
        assertEquals(ClientMsgType.GENERIC_ERROR, type)
    }

    @Test
    fun testFromStringProviderList() {
        // Arrange & Act
        val type = ClientMsgType.fromString("provider_request")

        // Assert
        assertEquals(ClientMsgType.PROVIDER_LIST, type)
    }

    @Test
    fun testFromStringInvalidTypeFails() {
        // Arrange & Act & Assert
        assertFailsWith<NoSuchElementException> {
            ClientMsgType.fromString("invalid_type")
        }
    }

    @Test
    fun testFromStringEmptyStringFails() {
        // Arrange & Act & Assert
        assertFailsWith<NoSuchElementException> {
            ClientMsgType.fromString("")
        }
    }

    @Test
    fun testClientMsgTypeStringValues() {
        // Assert
        assertEquals("generic_error", ClientMsgType.GENERIC_ERROR.str)
        assertEquals("provider_request", ClientMsgType.PROVIDER_LIST.str)
    }

    @Test
    fun testClientMsgTypeEntriesCount() {
        // Assert
        assertEquals(2, ClientMsgType.entries.size)
    }
}
