package net.kigawa.keruta.ktse.auth.oidc

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.server.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktse.auth.jwks.JwksProvider
import net.kigawa.keruta.ktse.auth.jwt.Auth0UnverifiedToken
import net.kigawa.keruta.ktse.auth.jwt.Auth0UnverifiedTokenWithKey
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url.Companion.parse

class KtorUnverifiedTokenWithOidc(
    val unverifiedToken: Auth0UnverifiedToken, val oidcConf: OidcConf,
    val jwksProvider: JwksProvider,
): UnverifiedTokenWithOidc {
    val keyId by unverifiedToken::keyId
    override fun useJwks(): Res<UnverifiedTokenWithKey, KtcpErr> = when (
        val res = jwksProvider.algorithmByUrl(
            parse(oidcConf.jwksUri),
            keyId ?: return Res.Err(VerifyFailErr("", null))
        )
    ) {
        is Res.Err -> return res.convert()
        is Res.Ok -> Res.Ok(Auth0UnverifiedTokenWithKey(unverifiedToken, res.value))
    }
}
