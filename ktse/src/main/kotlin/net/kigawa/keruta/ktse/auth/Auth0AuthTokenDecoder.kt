package net.kigawa.keruta.ktse.auth

import net.kigawa.keruta.ktcp.domain.auth.jwt.JwtVerifier
import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.UnverifiedAuthTokens
import net.kigawa.keruta.ktcp.server.auth.jwt.AuthTokenDecoder
import net.kigawa.kodel.api.err.Res

class Auth0AuthTokenDecoder(
    val jwtVerifier: JwtVerifier,
): AuthTokenDecoder {
    override fun decodeAuthRequestMsg(authRequestMsg: ServerAuthRequestMsg): Res<UnverifiedAuthTokens, KtcpErr> {
        val unverifiedUserToken = when (
            val res = jwtVerifier.decodeUnverified(authRequestMsg.userToken)
        ) {
            is Res.Err -> return res.convert()
            is Res.Ok -> res.value
        }
        val unverifiedProviderToken = when (
            val res = jwtVerifier.decodeUnverified(
                authRequestMsg.serverToken
            )
        ) {
            is Res.Err -> return res.convert()
            is Res.Ok -> res.value
        }
        return Res.Ok(
            UnverifiedAuthTokens(
                unverifiedUserToken, unverifiedProviderToken
            )
        )
    }
}
