package net.kigawa.keruta.ktse.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.model.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktse.auth.jwks.JwksProvider
import net.kigawa.keruta.ktse.auth.oidc.KtorUnverifiedTokenWithOidc
import net.kigawa.keruta.ktse.auth.oidc.OidcConfigProvider
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.dump.withStr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.convertOk
import net.kigawa.kodel.api.net.Url

class Auth0UnverifiedToken(
    val decode: DecodedJWT,
    val strToken: AuthToken,
    val oidcConfigProvider: OidcConfigProvider,
    val jwksProvider: JwksProvider,
): UnverifiedToken {

    override val subject: String by decode::subject
    override val issuer: Url get() = Url.parse(decode.issuer)
    val keyId: String? get() = decode.keyId

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


    val dump
        get() = Dumper.dump(
            this::class,
            ::decode withStr { it.str },
        )

    override fun toString(): String = dump.str()
}
