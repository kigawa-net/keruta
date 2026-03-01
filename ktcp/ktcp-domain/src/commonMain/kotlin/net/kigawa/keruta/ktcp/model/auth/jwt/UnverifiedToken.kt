package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.keruta.ktcp.model.auth.key.PrivateKey
import net.kigawa.keruta.ktcp.model.auth.oidc.UnverifiedTokenWithOidc
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.err.flatConvertOk
import net.kigawa.kodel.api.net.Url

interface UnverifiedToken {
    fun withKey(key: PrivateKey): Res<UnverifiedTokenWithKey, KtcpErr>
    suspend fun withOidcConfig(): Res<UnverifiedTokenWithOidc, KtcpErr>
    suspend fun verifyWithOidcJwks(
        userVerifyValues: JwtVerifyValues,
    ): Res<VerifiedToken, KtcpErr> = withOidcConfig()
        .flatConvertOk { it.useJwks() }
        .flatConvertOk { it.verify(userVerifyValues) }

    fun withJwks(): Res<UnverifiedTokenWithKey, KtcpErr>
    fun verifyWithJwks(verifyValues: JwtVerifyValues): Res<VerifiedToken, KtcpErr> = withJwks()
        .flatConvertOk { it.verify(verifyValues) }

    val subject: String
    val issuer: Url
}
