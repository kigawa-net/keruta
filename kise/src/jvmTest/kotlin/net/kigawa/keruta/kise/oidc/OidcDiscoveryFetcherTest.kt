package net.kigawa.keruta.kise.oidc

import kotlinx.serialization.json.Json
import net.kigawa.keruta.kise.oidc.model.OidcDiscoveryResponse
import net.kigawa.kodel.api.log.getKogger
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OidcDiscoveryFetcherTest {
    private val logger = getKogger()

    @Test
    fun `test OIDC discovery response parsing`() {
        // Mock JSON response
        val mockResponse = """
            {
                "issuer": "https://id.kigawa.net",
                "jwks_uri": "https://id.kigawa.net/jwks",
                "authorization_endpoint": "https://id.kigawa.net/auth",
                "token_endpoint": "https://id.kigawa.net/token"
            }
        """.trimIndent()

        // Verify we can parse it
        val response = Json.decodeFromString<OidcDiscoveryResponse>(mockResponse)

        assertNotNull(response)
        assertTrue(response.issuer == "https://id.kigawa.net")
        assertTrue(response.jwksUri == "https://id.kigawa.net/jwks")
        assertNotNull(response.authorizationEndpoint)
        assertNotNull(response.tokenEndpoint)

        logger.info("Parsed OIDC discovery response: $response")
    }
}
