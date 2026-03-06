package net.kigawa.keruta.ktcp.base.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.base.auth.jwks.JwksProvider
import net.kigawa.keruta.ktcp.base.auth.key.Auth0AlgorithmInitializer
import net.kigawa.keruta.ktcp.base.auth.key.JavaPrivateKeyInitializer
import net.kigawa.keruta.ktcp.base.auth.oidc.KtorUnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.base.auth.oidc.OidcConfigProvider
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import net.kigawa.keruta.ktcp.model.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.dump.withStr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.convertOk
import net.kigawa.kodel.api.err.ok
import net.kigawa.kodel.api.net.Url

class Auth0UnverifiedToken(
    val decode: DecodedJWT,
    val strToken: AuthToken,
    val oidcConfigProvider: OidcConfigProvider,
    val jwksProvider: JwksProvider,
    val auth0AlgorithmInitializer: Auth0AlgorithmInitializer,
    val javaPrivateKeyInitializer: JavaPrivateKeyInitializer,
): UnverifiedToken {
    override val subject: String by decode::subject
    override val issuer: Url get() = Url.parse(decode.issuer)
    val keyId: String? get() = decode.keyId
    override fun withKey(
        key: KerutaPrivateKey,
    ): Res<UnverifiedTokenWithKey, KtcpErr> = javaPrivateKeyInitializer.initialize(key)
        .let { auth0AlgorithmInitializer.initPrivateKey(it) }
        .let { Auth0UnverifiedTokenWithKey(this, it) }
        .ok()

    override suspend fun withOidcConfig(): Res<UnverifiedTokenWithOidc, KtcpErr> {
        return when (val res = oidcConfigProvider.get(issuer)) {
            is Res.Err -> res.convert()
            is Res.Ok -> Res.Ok(
                KtorUnverifiedTokenWithOidc(this, res.value, jwksProvider)
            )
        }
    }

    override fun withJwks(): Res<UnverifiedTokenWithKey, KtcpErr> = jwksProvider
        .algorithmByIssuer(
            issuer, keyId
        ).convertOk {
            Auth0UnverifiedTokenWithKey(this, it)
        }

    override fun toString(): String = Dumper.dump(
        this::class,
        ::decode withStr { "DecodedJWT(iss: ${it.issuer}, aud: ${it.audience}, sub: ${it.subject})" },
    ).str()
}
