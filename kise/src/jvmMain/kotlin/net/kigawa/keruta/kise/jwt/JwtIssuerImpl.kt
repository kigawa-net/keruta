package net.kigawa.keruta.kise.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.kise.usecase.auth.JwtIssuer
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

/**
 * JWT発行の実装
 */
class JwtIssuerImpl(
    private val publicKey: RSAPublicKey,
    private val privateKey: RSAPrivateKey,
    private val issuer: String,
    private val defaultAudience: String = "keruta",
    private val expiresInMs: Long = 3_600_000, // 1時間
) : JwtIssuer {

    private val algorithm = Algorithm.RSA256(publicKey, privateKey)

    override fun createToken(
        userId: Long,
        issuer: String,
        subject: String,
        audience: String,
    ): String {
        val expiresAt = Date(System.currentTimeMillis() + expiresInMs)
        val issuedAt = Date()

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId.toString())
            .withClaim("userId", userId)
            .withClaim("iss", issuer)
            .withClaim("sub", subject)
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt)
            .sign(algorithm)
    }

    /**
     * 設定からJwtIssuerを生成する
     */
    companion object {
        fun fromPem(
            publicKeyPem: String,
            privateKeyPem: String,
            issuer: String,
            audience: String = "keruta",
            expiresInMs: Long = 3_600_000,
        ): JwtIssuerImpl {
            val publicKey = loadPublicKeyFromPem(publicKeyPem)
            val privateKey = loadPrivateKeyFromPem(privateKeyPem)
            return JwtIssuerImpl(publicKey, privateKey, issuer, audience, expiresInMs)
        }

        private fun loadPublicKeyFromPem(pem: String): RSAPublicKey {
            val keyFactory = KeyFactory.getInstance("RSA")
            val keyBytes = decodePem(pem)
            val keySpec = X509EncodedKeySpec(keyBytes)
            return keyFactory.generatePublic(keySpec) as RSAPublicKey
        }

        private fun loadPrivateKeyFromPem(pem: String): RSAPrivateKey {
            val keyFactory = KeyFactory.getInstance("RSA")
            val keyBytes = decodePem(pem)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        }

        private fun decodePem(pem: String): ByteArray {
            val lines = pem.lines()
                .filter { !it.startsWith("-----") }
                .joinToString("")
            return Base64.getDecoder().decode(lines)
        }
    }
}