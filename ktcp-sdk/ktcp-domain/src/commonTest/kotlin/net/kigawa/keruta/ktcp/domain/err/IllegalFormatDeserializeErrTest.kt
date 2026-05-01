package net.kigawa.keruta.ktcp.domain.err

import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class IllegalFormatDeserializeErrTest {
    @Test
    fun testCreateIllegalFormatDeserializeErr() {
        // Arrange
        val cause = SerializationException("Test serialization error")

        // Act
        val err = IllegalFormatDeserializeErr("Deserialization failed", cause)

        // Assert
        assertEquals("Deserialization failed", err.message)
        assertIs<SerializationException>(err.cause)
        assertEquals("Test serialization error", err.cause?.message)
    }

    @Test
    fun testIllegalFormatDeserializeErrCode() {
        // Arrange
        val cause = SerializationException("Test error")
        val err = IllegalFormatDeserializeErr("Test", cause)

        // Act
        val code = err.code

        // Assert
        assertEquals(CommonErrCode.ILLEGAL_FORMAT_DESERIALIZE.name, code)
        assertEquals("ILLEGAL_FORMAT_DESERIALIZE", code)
    }

    @Test
    fun testIllegalFormatDeserializeErrExtendsKtcpErr() {
        // Arrange
        val cause = SerializationException("Test error")
        val err = IllegalFormatDeserializeErr("Test", cause)

        // Assert
        assertIs<KtcpErr>(err)
    }

    @Test
    fun testIllegalFormatDeserializeErrWithEmptyMessage() {
        // Arrange
        val cause = SerializationException("Test error")

        // Act
        val err = IllegalFormatDeserializeErr("", cause)

        // Assert
        assertEquals("", err.message)
        assertNotNull(err.cause)
    }
}
