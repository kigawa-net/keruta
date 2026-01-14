package net.kigawa.keruta.ktse.auth

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktcp.server.auth.UnverifiedToken
import net.kigawa.keruta.ktcp.server.auth.VerifiedToken
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktcp.server.err.VerifyUnsupportedKeyErr
import net.kigawa.kodel.api.cache.LruCache
import net.kigawa.kodel.api.err.Res
import java.net.URL
import java.security.interfaces.RSAPublicKey

class Auth0JwtVerifier: JwtVerifier {
    val providers = LruCache<String, JwkProvider>(8)
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

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
    ): Res<VerifiedToken, VerifyErr> {
        val jwksUrl = when (val res = getJwksUrl(idpConfig.issuer)) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        val provider = providers.getOrPut(idpConfig.issuer) {
            JwkProviderBuilder(jwksUrl).build()
        }
        val key = provider.get(rawToken.keyId)
        val alg = when (val alg = alg(key)) {
            is Res.Err<*, VerifyUnsupportedKeyErr> -> return alg.x()
            is Res.Ok<Algorithm, *> -> alg.value
        }
        val verifier = verifier(alg, idpConfig.issuer, idpConfig.audience, subject)
        return try {
            val verified = verifier.verify(token)
            Res.Ok(Auth0VerifiedToken(verified))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("", e))
        }
    }

    suspend fun getJwksUrl(issuer: String): Res<URL, VerifyErr> {
        val res = client.get("$issuer/.well-known/openid-configuration")
        if (!res.status.isSuccess()) return Res.Err(VerifyFailErr("getJwksUrl", null))
        return try {
            Res.Ok(URL(res.body<OidcConf>().jwksUrl))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("getJwksUrl", e))
        }
    }

    private fun verifier(
        alg: Algorithm, issuer: String, audience: String,
        subject: String,
    ) = JWT.require(alg)
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(subject)
        .build()

    private fun alg(key: Jwk): Res<Algorithm, VerifyUnsupportedKeyErr> = when (
        val pub = key.publicKey
    ) {
        is RSAPublicKey -> Res.Ok(Algorithm.RSA256(pub, null))
        else -> Res.Err(VerifyUnsupportedKeyErr(pub.toString(), null))
    }

}
