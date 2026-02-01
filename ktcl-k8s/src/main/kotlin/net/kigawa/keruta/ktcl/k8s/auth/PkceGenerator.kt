package net.kigawa.keruta.ktcl.k8s.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class PkceGenerator {
    private val secureRandom = SecureRandom()

    fun generate(): Pkce {
        val codeVerifier = generateCodeVerifier()
        return Pkce(
            codeVerifier = codeVerifier,
            codeChallenge = generateCodeChallenge(codeVerifier),
            state = generateState(),
            nonce = generateNonce()
        )
    }

    private fun generateCodeVerifier(): CodeVerifier {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun generateCodeChallenge(codeVerifier: String): CodeChallenge {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }

    private fun generateState(): State {
        val bytes = ByteArray(16)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun generateNonce(): Nonce {
        val bytes = ByteArray(16)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
