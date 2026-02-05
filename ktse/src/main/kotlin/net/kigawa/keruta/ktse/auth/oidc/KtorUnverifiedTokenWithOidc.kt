package net.kigawa.keruta.ktse.auth.oidc

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.server.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktse.auth.jwks.JwksConfigProvider
import net.kigawa.keruta.ktse.auth.jwt.Auth0UnverifiedToken
import net.kigawa.keruta.ktse.auth.jwt.Auth0UnverifiedTokenWithKey
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url.Companion.parse

class KtorUnverifiedTokenWithOidc(
    val unverifiedToken: Auth0UnverifiedToken, val oidcConf: OidcConf,
    val jwksConfigProvider: JwksConfigProvider,
): UnverifiedTokenWithOidc {
    val keyId by unverifiedToken::keyId
    override fun useJwks(): Res<UnverifiedTokenWithKey, KtcpErr> = when (
        val res = jwksConfigProvider.algorithmByUrl(
            parse(oidcConf.jwksUri), keyId
        )
    ) {
        is Res.Err -> return res.x()
        is Res.Ok -> Res.Ok(Auth0UnverifiedTokenWithKey(unverifiedToken, res.value))
    }
}
