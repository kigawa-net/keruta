package net.kigawa.keruta.ktse.auth

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.UnverifiedToken
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.auth.jwks.JwksConfigProvider
import net.kigawa.keruta.ktse.auth.oidc.OidcConfigProvider
import net.kigawa.kodel.api.err.Res

class UnverifiedAuthTokens(
    val userToken: UnverifiedToken,
    val providerToken: UnverifiedToken,
    val jwksConfigProvider: JwksConfigProvider,
    val oidcConfigProvider: OidcConfigProvider
) {
    suspend fun verify(tables: PersistedVerifyTables): Res<Unit, KtcpErr> {
        val userOidcConf = when (
            val res =oidcConfigProvider.get(userToken.issuer)
        ) {
            is Res.Err-> return res.x()
            is Res.Ok-> res.value
        }
        val userJwksConf = jwksConfigProvider.getByUrl(userOidcConf.jwksUri)
        userToken.verify(tables.userIdp.asUserIdp(), false)
        TODO("Not yet implemented")
    }
}
