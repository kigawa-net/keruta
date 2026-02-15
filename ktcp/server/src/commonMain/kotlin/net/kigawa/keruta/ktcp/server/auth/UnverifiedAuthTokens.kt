package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.convertOk
import net.kigawa.kodel.api.err.with

class UnverifiedAuthTokens(
    val userToken: UnverifiedToken,
    val providerToken: UnverifiedToken,
) {
    val subject: String by userToken::subject

    suspend fun verify(
        userVerifyValues: JwtVerifyValues, providerVerifyValues: JwtVerifyValues,
    ): Res<VerifiedAuthToken, KtcpErr> = userToken.verifyWithOidcJwks(userVerifyValues)
        .with(providerToken.verifyWithJwks(providerVerifyValues))
        .convertOk {
            VerifiedAuthToken(it.first, it.second)
        }
}
