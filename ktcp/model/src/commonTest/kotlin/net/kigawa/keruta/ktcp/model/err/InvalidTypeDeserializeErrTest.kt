package net.kigawa.keruta.ktcp.model.err

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class InvalidTypeDeserializeErrTest {
    @Test
    fun testCreateInvalidTypeDeserializeErr() {
        // Arrange
        val cause = IllegalArgumentException("Invalid type")

        // Act
        val err = InvalidTypeDeserializeErr("Type validation failed", cause)

        // Assert
        assertEquals("Type validation failed", err.message)
        assertIs<IllegalArgumentException>(err.cause)
        assertEquals("Invalid type", err.cause?.message)
    }

    @Test
    fun testInvalidTypeDeserializeErrCode() {
        // Arrange
        val cause = IllegalArgumentException("Test error")
        val err = InvalidTypeDeserializeErr("Test", cause)

        // Act
        val code = err.code

        // Assert
        assertEquals(CommonErrCode.INVALID_TYPE_DESERIALIZE.name, code)
        assertEquals("INVALID_TYPE_DESERIALIZE", code)
    }

    @Test
    fun testInvalidTypeDeserializeErrExtendsKtcpErr() {
        // Arrange
        val cause = IllegalArgumentException("Test error")
        val err = InvalidTypeDeserializeErr("Test", cause)

        // Assert
        assertIs<KtcpErr>(err)
    }

    @Test
    fun testInvalidTypeDeserializeErrWithEmptyMessage() {
        // Arrange
        val cause = IllegalArgumentException("Test error")

        // Act
        val err = InvalidTypeDeserializeErr("", cause)

        // Assert
        assertEquals("", err.message)
        assertNotNull(err.cause)
    }
}