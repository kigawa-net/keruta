package net.kigawa.keruta.ktse.auth.jwks

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.err.VerifyUnsupportedKeyErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url
import java.security.interfaces.RSAPublicKey

class JwksConfigProvider {
    fun algorithmByUrl(jwksUrl: Url, keyId: String): Res<Algorithm, KtcpErr> = when (
        val pub = JwkProviderBuilder(jwksUrl.toJvmUrl()).build().get(keyId).publicKey
    ) {
        is RSAPublicKey -> Res.Ok(Algorithm.RSA256(pub, null))
        else -> Res.Err(VerifyUnsupportedKeyErr(pub.toString(), null))
    }
}
