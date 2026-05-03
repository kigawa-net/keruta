package net.kigawa.keruta.kise

import net.kigawa.keruta.kise.domain.entity.AuthResult
import net.kigawa.keruta.kise.domain.entity.Session
import net.kigawa.keruta.kise.domain.error.KiseErr
import net.kigawa.keruta.kise.domain.repository.ProviderRepository
import net.kigawa.keruta.kise.domain.repository.SessionRepository
import net.kigawa.keruta.kise.domain.repository.UserRepository
import net.kigawa.keruta.kise.jwt.JwtIssuerImpl
import net.kigawa.keruta.kise.persist.repository.ExposedProviderRepository
import net.kigawa.keruta.kise.persist.repository.ExposedSessionRepository
import net.kigawa.keruta.kise.persist.repository.ExposedUserRepository
import net.kigawa.keruta.kise.usecase.auth.AuthenticateUseCase
import net.kigawa.keruta.kise.usecase.auth.JwtIssuer
import net.kigawa.keruta.kise.usecase.session.LogoutUseCase
import net.kigawa.keruta.kise.usecase.session.RefreshSessionUseCase
import net.kigawa.keruta.kise.usecase.session.VerifySessionUseCase
import net.kigawa.kodel.api.err.Res

/**
 * Kise 認証サービス
 *
 * すべてのユースケースとリポジトリをまとめたサービス
 */
class KiseService(config: KiseConfig) {
    // リポジトリ
    private val userRepository: UserRepository = ExposedUserRepository()
    private val providerRepository: ProviderRepository = ExposedProviderRepository()
    private val sessionRepository: SessionRepository = ExposedSessionRepository()

    // JWT発行
    private val jwtIssuer: JwtIssuer = JwtIssuerImpl.fromPem(
        publicKeyPem = config.jwtPublicKey,
        privateKeyPem = config.jwtPrivateKey,
        issuer = config.issuer,
        audience = config.audience,
        expiresInMs = config.tokenExpiresInMs
    )

    // ユースケース
    private val authenticateUseCase = AuthenticateUseCase(
        userRepository = userRepository,
        providerRepository = providerRepository,
        sessionRepository = sessionRepository,
        jwtIssuer = jwtIssuer
    )

    private val verifySessionUseCase = VerifySessionUseCase(sessionRepository)
    private val refreshSessionUseCase = RefreshSessionUseCase(sessionRepository)
    private val logoutUseCase = LogoutUseCase(sessionRepository)

    /**
     * 認証を実行する
     */
    suspend fun authenticate(input: AuthenticateUseCase.AuthInput): Res<AuthResult, KiseErr> {
        // Note: 実際のJWT検証は ktcp-sdk の Auth0JwtVerifier を使用するため、
        // ここでは stub 実装を提供します
        return authenticateUseCase.execute(input)
    }

    /**
     * セッションを検証する
     */
    suspend fun verifySession(token: String, currentTime: Long): Res<Session, KiseErr> {
        return verifySessionUseCase.execute(token, currentTime)
    }

    /**
     * セッションを更新する
     */
    suspend fun refreshSession(token: String, currentTime: Long): Res<Session, KiseErr> {
        return refreshSessionUseCase.execute(token, currentTime)
    }

    /**
     * ログアウト（セッション削除）
     */
    suspend fun logout(token: String): Res<Unit, KiseErr> {
        return logoutUseCase.execute(token)
    }
}