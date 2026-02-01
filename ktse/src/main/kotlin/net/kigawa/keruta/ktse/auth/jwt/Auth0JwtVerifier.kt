package net.kigawa.keruta.ktse.auth.jwt

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.UnverifiedToken
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktcp.server.err.VerifyUnsupportedKeyErr
import net.kigawa.keruta.ktse.auth.jwks.JwksProvider
import net.kigawa.keruta.ktse.http.HttpClient
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.coroutine.cache.ConcurrentLruCache
import java.security.interfaces.RSAPublicKey

class Auth0JwtVerifier(
    httpClient: HttpClient,
): JwtVerifier {
    val providers = ConcurrentLruCache<String, JwkProvider>(8)
    val verifierProvider = VerifierProvider()
    val jwksProvider = JwksProvider(httpClient)


    override fun decodeUnverified(
        userToken: AuthToken,
    ): Res<UnverifiedToken, VerifyErr> = try {
        Res.Ok(
            Auth0UnverifiedToken(JWT.decode(userToken), this, userToken)
        )
    } catch (e: Exception) {
        Res.Err(VerifyFailErr("decode", e))
    }

    suspend fun verify(
        token: AuthToken,
        rawToken: DecodedJWT,
        subject: String,
        idpConfig: IdpConfig,
        oidc: Boolean,
        alg: Algorithm,
    ): Res<VerifiedToken, VerifyErr> {
//        val provider = if (oidc) {
//            val jwksUrl = when (val res = jwksProvider.getJwksUrl(idpConfig.issuer)) {
//                is Res.Err -> return res.x()
//                is Res.Ok -> res.value
//            }
//            providers.use {
//                getOrPut(idpConfig.issuer) {
//                    JwkProviderBuilder(jwksUrl).build()
//                }
//            }
//        } else providers.use {
//            getOrPut(idpConfig.issuer) {
//                JwkProviderBuilder(idpConfig.issuer).build()
//            }
//        }
//        val key = provider.get(rawToken.keyId)
//        val alg = when (val alg = alg(key)) {
//            is Res.Err<*, VerifyUnsupportedKeyErr> -> return alg.x()
//            is Res.Ok<Algorithm, *> -> alg.value
//        }
        val verifier = verifierProvider.verifier(alg, idpConfig.issuer, idpConfig.audience, subject)
        return try {
            val verified = verifier.verify(token)
            Res.Ok(Auth0VerifiedToken(verified))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("token: ${rawToken.str}", e))
        }
    }

    private fun alg(key: Jwk): Res<Algorithm, VerifyUnsupportedKeyErr> = when (
        val pub = key.publicKey
    ) {
        is RSAPublicKey -> Res.Ok(Algorithm.RSA256(pub, null))
        else -> Res.Err(VerifyUnsupportedKeyErr(pub.toString(), null))
    }

    val dump
        get() = Dumper.dump(
            this::class,
        )

    override fun toString(): String = dump.str()
}
