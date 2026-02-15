package net.kigawa.keruta.ktcp.base.auth.jwt

import com.auth0.jwt.JWT
import net.kigawa.keruta.ktcp.base.auth.VerifyFailErr
import net.kigawa.keruta.ktcp.base.auth.jwks.JwksProvider
import net.kigawa.keruta.ktcp.base.auth.oidc.OidcConfigProvider
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifier
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.err.Res

class Auth0JwtVerifier(
    val oidcConfigProvider: OidcConfigProvider,
    val jwksProvider: JwksProvider,
): JwtVerifier {


    override fun decodeUnverified(
        userToken: AuthToken,
    ): Res<UnverifiedToken, VerifyErr> = try {
        Res.Ok(
            Auth0UnverifiedToken(
                JWT.decode(userToken), userToken,
                oidcConfigProvider, jwksProvider
            )
        )
    } catch (e: Exception) {
        Res.Err(VerifyFailErr("decode", e))
    }

    val dump
        get() = Dumper.dump(
            this::class,
        )

    override fun toString(): String = dump.str()
}
