package net.kigawa.keruta.ktcp.base.auth.jwks

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.base.auth.oidc.VerifyUnsupportedKeyErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey

class JwksProvider {
    fun algorithmByUrl(jwksUrl: Url, keyId: String): Res<Algorithm, KtcpErr> = convertAlgorithm(
        JwkProviderBuilder(jwksUrl.toJvmUrl()).build().get(keyId).publicKey
    )

    fun algorithmByIssuer(issuer: Url, keyId: String?): Res<Algorithm, KtcpErr> = convertAlgorithm(
        JwkProviderBuilder(issuer.toStrUrl()).build().get(keyId).publicKey
    )

    private fun convertAlgorithm(key: PublicKey): Res<Algorithm, KtcpErr> = when (key) {
        is RSAPublicKey -> Res.Ok(Algorithm.RSA256(key, null))
        else -> Res.Err(VerifyUnsupportedKeyErr(key.toString(), null))
    }
}
