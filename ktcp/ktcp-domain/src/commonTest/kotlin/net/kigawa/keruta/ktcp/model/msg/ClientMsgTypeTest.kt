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
        val type = ClientMsgType.fromString("provider_listed")

        // Assert
        assertEquals(ClientMsgType.PROVIDER_LISTED, type)
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
        assertEquals("provider_listed", ClientMsgType.PROVIDER_LISTED.str)
    }

}
