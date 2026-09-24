package net.kigawa.keruta.kise.oidc

import net.kigawa.kodel.api.log.getKogger
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PkceGeneratorTest {
    private val logger = getKogger()
    private val generator = PkceGenerator()

    @Test
    fun `test Pkce generation`() {
        val pkce = generator.generate()

        assertNotNull(pkce.codeVerifier)
        assertNotNull(pkce.codeChallenge)
        assertNotNull(pkce.state)
        assertNotNull(pkce.nonce)

        logger.info("Generated PKCE: codeVerifier=${pkce.codeVerifier}, codeChallenge=${pkce.codeChallenge}")

        // code_challenge should be base64url encoded SHA-256 hash of code_verifier
        assertTrue(pkce.codeVerifier.isNotEmpty())
        assertTrue(pkce.codeChallenge.isNotEmpty())
        assertTrue(pkce.state.isNotEmpty())
        assertTrue(pkce.nonce.isNotEmpty())
    }

    @Test
    fun `test multiple generations produce different values`() {
        val pkce1 = generator.generate()
        val pkce2 = generator.generate()

        // State and nonce should be random, so they should be different
        assertTrue(pkce1.state != pkce2.state)
        assertTrue(pkce1.nonce != pkce2.nonce)
    }
}
