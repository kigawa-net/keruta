package net.kigawa.keruta.ktse.auth

import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.JwtVerifier
import net.kigawa.keruta.ktse.auth.jwks.JwksConfigProvider
import net.kigawa.keruta.ktse.auth.oidc.OidcConfigProvider
import net.kigawa.kodel.api.err.Res

class AuthTokenVerifier(
    val jwtVerifier: JwtVerifier,
    val jwksConfigProvider: JwksConfigProvider,
    val oidcConfigProvider: OidcConfigProvider,
) {
    fun decodeAuthRequestMsg(authRequestMsg: ServerAuthRequestMsg): Res<UnverifiedAuthTokens, KtcpErr> {
        val unverifiedUserToken = when (
            val res = jwtVerifier.decodeUnverified(authRequestMsg.userToken)
        ) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        val unverifiedProviderToken = when (
            val res = jwtVerifier.decodeUnverified(
                authRequestMsg.serverToken
            )
        ) {
            is Res.Err -> return res.x()
            is Res.Ok -> res.value
        }
        return Res.Ok(
            UnverifiedAuthTokens(
                unverifiedUserToken, unverifiedProviderToken, jwksConfigProvider, oidcConfigProvider
            )
        )
    }
}
