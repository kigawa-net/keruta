package net.kigawa.keruta.ktse.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.server.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktse.auth.oidc.KtorUnverifiedTokenWithOidc
import net.kigawa.keruta.ktse.auth.oidc.OidcConfigProvider
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.dump.withStr
import net.kigawa.kodel.api.err.Res

class Auth0UnverifiedToken(
    val decode: DecodedJWT,
    val verifier: Auth0JwtVerifier,
    val strToken: AuthToken,
    val oidcConfigProvider: OidcConfigProvider,
): UnverifiedToken {

    override val subject: String by decode::subject
    override val issuer: String by decode::issuer
    val keyId: String by decode::keyId

    override suspend fun withOidcConfig(): Res<UnverifiedTokenWithOidc, KtcpErr> {
        return when (val res = oidcConfigProvider.get(issuer)) {
            is Res.Err -> res.x()
            is Res.Ok -> Res.Ok(KtorUnverifiedTokenWithOidc(this, res.value))
        }
    }


    val dump
        get() = Dumper.dump(
            this::class,
            ::decode withStr { it.str },
        )

    override fun toString(): String = dump.str()
}
