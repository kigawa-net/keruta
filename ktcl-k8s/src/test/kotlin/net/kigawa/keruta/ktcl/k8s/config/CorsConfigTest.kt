package net.kigawa.keruta.ktcl.k8s.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CorsConfigTest {

    @Test
    fun `fromString should return null origins when input is null`() {
        // When
        val config = CorsConfig.fromString(null)

        // Then
        assertNull(config.allowedOrigins)
    }

    @Test
    fun `fromString should return null origins when input is empty`() {
        // When
        val config = CorsConfig.fromString("")

        // Then
        assertEquals(emptyList<String>(), config.allowedOrigins)
    }

    @Test
    fun `fromString should parse single origin correctly`() {
        // Given
        val originsString = "https://example.com"

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(listOf("https://example.com"), config.allowedOrigins)
    }

    @Test
    fun `fromString should parse multiple origins correctly`() {
        // Given
        val originsString = "https://example.com,http://localhost:3000"

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(
            listOf("https://example.com", "http://localhost:3000"),
            config.allowedOrigins
        )
    }

    @Test
    fun `fromString should trim whitespace from origins`() {
        // Given
        val originsString = " https://example.com , http://localhost:3000 "

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(
            listOf("https://example.com", "http://localhost:3000"),
            config.allowedOrigins
        )
    }

    @Test
    fun `fromString should filter out empty strings`() {
        // Given
        val originsString = "https://example.com,,http://localhost:3000"

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(
            listOf("https://example.com", "http://localhost:3000"),
            config.allowedOrigins
        )
    }

    @Test
    fun `fromString should handle origins with ports`() {
        // Given
        val originsString = "http://localhost:3000,http://127.0.0.1:8080"

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(
            listOf("http://localhost:3000", "http://127.0.0.1:8080"),
            config.allowedOrigins
        )
    }

    @Test
    fun `fromString should handle mixed http and https origins`() {
        // Given
        val originsString = "https://prod.example.com,http://dev.example.com:3000"

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(
            listOf("https://prod.example.com", "http://dev.example.com:3000"),
            config.allowedOrigins
        )
    }

    @Test
    fun `fromString should handle wildcard origins`() {
        // Given
        val originsString = "https://*.example.com"

        // When
        val config = CorsConfig.fromString(originsString)

        // Then
        assertEquals(listOf("https://*.example.com"), config.allowedOrigins)
    }
}
