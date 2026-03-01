package net.kigawa.keruta.ktcl.k8s.web.auth

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
import java.security.interfaces.RSAPublicKey

class JwkUnverifiedToken(
    private val decodedJwt: DecodedJWT,
    private val jwkProvider: JwkProvider,
) : UnverifiedToken {
    override val subject: String = decodedJwt.subject
    override val issuer: Url = Url.parse(decodedJwt.issuer)

    override fun withKey(key: PrivateKey): Res<UnverifiedTokenWithKey, KtcpErr> {
        // JWKベースのトークンでは直接キーを使用できない
        return VerifyErr("withKey_not_supported", "JWK-based tokens don't support direct key", null).err()
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
