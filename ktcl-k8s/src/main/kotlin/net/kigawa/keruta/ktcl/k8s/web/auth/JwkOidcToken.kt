package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.keruta.ktcp.model.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.err
import net.kigawa.kodel.api.err.ok
import java.security.interfaces.RSAPublicKey

class JwkOidcToken(
    private val decodedJwt: DecodedJWT,
    private val jwkProvider: JwkProvider,
) : UnverifiedTokenWithOidc {
    override fun useJwks(): Res<UnverifiedTokenWithKey, KtcpErr> {
        return try {
            val jwk = jwkProvider.get(decodedJwt.keyId)
            val publicKey = jwk.publicKey as RSAPublicKey
            JwkUnverifiedTokenWithKey(decodedJwt, publicKey).ok()
        } catch (e: Exception) {
            VerifyErr("jwk_fetch_failed", e.message, e).err()
        }
    }
}
