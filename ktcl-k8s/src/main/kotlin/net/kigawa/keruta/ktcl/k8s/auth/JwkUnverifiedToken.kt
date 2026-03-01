package net.kigawa.keruta.ktcl.k8s.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.keruta.ktcp.model.auth.key.PrivateKey
import net.kigawa.keruta.ktcp.model.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.err
import net.kigawa.kodel.api.err.ok
import net.kigawa.kodel.api.net.Url
import java.net.URI
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

class JwkUnverifiedToken(
    private val decodedJwt: DecodedJWT,
    private val jwkProvider: JwkProvider,
): UnverifiedToken {
    override val subject: String = decodedJwt.subject
    override val issuer: Url = Url.parse(decodedJwt.issuer)

    override fun withKey(key: PrivateKey): Res<UnverifiedTokenWithKey, KtcpErr> {
        return try {
            val pemKey = key.strKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\s".toRegex(), "")

            val keyBytes = Base64.getDecoder().decode(pemKey)
            val keyFactory = KeyFactory.getInstance("RSA")
            val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes))
            val publicKey = X509EncodedKeySpec(
                keyFactory.getKeySpec(privateKey, X509EncodedKeySpec::class.java).encoded
            ).let { keyFactory.generatePublic(it) }

            JwkUnverifiedTokenWithKey(decodedJwt, publicKey as RSAPublicKey).ok()
        } catch (e: Exception) {
            VerifyErr("with_key_failed", e.message, e).err()
        }
    }

    override suspend fun withOidcConfig(): Res<UnverifiedTokenWithOidc, KtcpErr> {
        return try {
            OidcDiscoveryFetcher().fetchByIssuer(URI(decodedJwt.issuer))
            JwkOidcToken(decodedJwt, jwkProvider).ok()
        } catch (e: Exception) {
            VerifyErr("oidc_discovery_failed", e.message, e).err()
        }
    }

    override fun withJwks(): Res<UnverifiedTokenWithKey, KtcpErr> {
        return try {
            val jwk = jwkProvider.get(decodedJwt.keyId)
            val publicKey = jwk.publicKey as RSAPublicKey
            JwkUnverifiedTokenWithKey(decodedJwt, publicKey).ok()
        } catch (e: Exception) {
            VerifyErr("jwk_fetch_failed", e.message, e).err()
        }
    }
}
