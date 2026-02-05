package net.kigawa.keruta.ktse.auth

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.auth.jwks.JwksConfigProvider
import net.kigawa.keruta.ktse.auth.oidc.OidcConfigProvider
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.net.Url

class UnverifiedAuthTokens(
    val userToken: UnverifiedToken,
    val providerToken: UnverifiedToken,
    val jwksConfigProvider: JwksConfigProvider,
    val oidcConfigProvider: OidcConfigProvider,
) {
    suspend fun verify(tables: PersistedVerifyTables): Res<Unit, KtcpErr> {
        val oidc = when (val res = userToken.withOidcConfig()) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        oidc.useJwks()

        TODO("Not yet implemented")
    }
}
