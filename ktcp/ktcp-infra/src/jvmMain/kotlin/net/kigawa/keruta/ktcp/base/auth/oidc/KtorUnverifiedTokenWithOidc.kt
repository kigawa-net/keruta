package net.kigawa.keruta.ktcp.base.auth.oidc

import net.kigawa.keruta.ktcp.base.auth.VerifyFailErr
import net.kigawa.keruta.ktcp.base.auth.jwks.JwksProvider
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0UnverifiedToken
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.domain.auth.jwt.UnverifiedTokenWithKey
import net.kigawa.keruta.ktcp.domain.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url

class KtorUnverifiedTokenWithOidc(
    val unverifiedToken: Auth0UnverifiedToken, val oidcConf: OidcConf,
    val jwksProvider: JwksProvider,
): UnverifiedTokenWithOidc {
    val keyId by unverifiedToken::keyId
    override fun useJwks(): Res<UnverifiedTokenWithKey, KtcpErr> = when (
        val res = jwksProvider.algorithmByUrl(
            Url.parse(oidcConf.jwksUri),
            keyId ?: return Res.Err(VerifyFailErr("", null))
        )
    ) {
        is Res.Err -> return res.convert()
        is Res.Ok -> Res.Ok(Auth0UnverifiedTokenWithKey(unverifiedToken, res.value))
    }
}
