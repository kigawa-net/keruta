package net.kigawa.keruta.kicl.usecase.kicp

import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.token.OidcToken
import net.kigawa.keruta.kicp.domain.token.ProviderToken
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCase
import net.kigawa.keruta.kicp.usecase.register.RegisterInput
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenInput
import net.kigawa.keruta.kicp.domain.identity.IdentityId
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.kodel.api.err.Res

/**
 * kicpクライアントを管理するクラス
 * kicl-webからkicpクライアントを登録・認証するための機能を提供
 */
class KicpClientManager(
    private val registerUseCase: RegisterUseCase,
    private val getRegisterTokenUseCase: GetRegisterTokenUseCase,
) {
    /**
     * 登録トークンを取得する
     * @param identityId 登録を行うID（通常は自分のIDサーバーのID）
     * @param validForMs トークンの有効期限（ミリ秒）
     */
    suspend fun getRegisterToken(
        identityId: String,
        validForMs: Long = 5 * 60 * 1000L
    ): Res<RegisterToken, KicpErr> {
        val input = GetRegisterTokenInput(
            identityId = IdentityId(identityId),
            validForMs = validForMs
        )
        return getRegisterTokenUseCase.getRegisterToken(input)
    }

    /**
     * kicpクライアントを登録する
     * @param oidcToken OIDCトークン
     * @param oidcJwksUrl OIDC JWKS URL
     * @param providerToken プロバイダートークン
     * @param providerJwksUrl プロバイダーJWKS URL
     * @param registerToken 登録トークン
     */
    suspend fun register(
        oidcToken: String,
        oidcJwksUrl: String,
        providerToken: String,
        providerJwksUrl: String,
        registerToken: String,
    ): Res<Unit, KicpErr> {
        val input = RegisterInput(
            oidcToken = OidcToken(oidcToken),
            oidcJwksUrl = JwksUrl(oidcJwksUrl),
            providerToken = ProviderToken(providerToken),
            providerJwksUrl = JwksUrl(providerJwksUrl),
            registerToken = RegisterToken(registerToken),
        )
        return when (val result = registerUseCase.register(input)) {
            is Res.Err -> Res.Err(result.err)
            is Res.Ok -> Res.Ok(Unit)
        }
    }
}
