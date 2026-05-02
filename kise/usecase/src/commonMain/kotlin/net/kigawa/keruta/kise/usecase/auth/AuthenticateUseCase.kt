package net.kigawa.keruta.kise.usecase.auth

import net.kigawa.keruta.kise.domain.entity.AuthResult
import net.kigawa.keruta.kise.domain.entity.Session
import net.kigawa.keruta.kise.domain.entity.UserIdp
import net.kigawa.keruta.kise.domain.error.InvalidTokenErr
import net.kigawa.keruta.kise.domain.error.KiseErr
import net.kigawa.keruta.kise.domain.error.UserNotFoundErr
import net.kigawa.keruta.kise.domain.repository.ProviderRepository
import net.kigawa.keruta.kise.domain.repository.SessionRepository
import net.kigawa.keruta.kise.domain.repository.UserRepository
import net.kigawa.kodel.api.err.Res
import kotlin.random.Random

/**
 * 認証ユースケース
 *
 * ユーザートークンとプロバイダートークンを検証し、セッションを作成/更新する
 */
class AuthenticateUseCase(
    private val userRepository: UserRepository,
    private val providerRepository: ProviderRepository,
    private val sessionRepository: SessionRepository,
    private val jwtIssuer: JwtIssuer,
) {
    /**
     * 認証リクエストの入力
     */
    data class AuthInput(
        val userToken: String,
        val providerToken: String,
        val defaultUserIdpIssuer: String,
        val defaultProviderIssuer: String,
    )

    /**
     * 認証を実行する
     */
    suspend fun execute(input: AuthInput): Res<AuthResult, KiseErr> {
        // 1. ユーザートークンの検証（OIDC）
        // 2. プロバイダートークンの検証
        // 3. ユーザーの作成/取得
        // 4. セッションの作成
        // 5. JWTトークンの発行

        // Note: 実際のJWT検証は ktcp-sdk の Auth0JwtVerifier を使用するため、
        // ここでは単純な実装とします
        // 本格的な実装では ktcp-sdk の認証コンポーネントを統合する必要があります

        return Res.Err(InvalidTokenErr("Not implemented yet - need to integrate with ktcp-sdk"))
    }

    /**
     * ユーザーIDP信息に基づいてユーザーを特定または作成する
     */
    private suspend fun findOrCreateUser(
        issuer: String,
        subject: String,
        audience: String,
    ): Res<Pair<UserIdp, Long>, KiseErr> {
        // 既存のユーザーを検索
        val existingUserIdp = userRepository.getUserIdpByIdentity(issuer, subject)
        if (existingUserIdp != null) {
            return Res.Ok(Pair(existingUserIdp, existingUserIdp.userId))
        }

        // 新規ユーザーの作成
        val user = userRepository.create()
        val userIdp = userRepository.createUserIdp(
            UserIdp(
                userId = user.id,
                providerId = 0, // あとで設定
                issuer = issuer,
                subject = subject,
                audience = audience,
            )
        )

        return Res.Ok(Pair(userIdp, user.id))
    }

    /**
     * セッションを作成し、JWTトークンを発行する
     */
    private suspend fun createSession(userId: Long, issuer: String, subject: String, currentTime: Long): Res<AuthResult, KiseErr> {
        // セッション数の制限を確認（最大10セッション/ユーザー）
        val sessionCount = sessionRepository.countByUserId(userId)
        if (sessionCount >= 10) {
            // 古いセッションを削除
            sessionRepository.deleteExpired(userId)
        }

        // JWTトークンの生成
        val token = generateToken(userId, issuer, subject)
        val expiresAt = currentTime + 3_600_000 // 1時間

        val session = sessionRepository.create(
            Session(
                userId = userId,
                token = token,
                expiresAt = expiresAt,
            )
        )

        // ユーザーの取得
        val user = userRepository.getById(userId)
            ?: return Res.Err(UserNotFoundErr("User not found after creation"))

        return Res.Err(InvalidTokenErr("Not implemented yet"))
    }

    private fun generateToken(userId: Long, issuer: String, subject: String): String {
        // 簡易的なトークン生成（実際はJWTを使用）
        val random = Random.nextBytes(32)
        return "$userId:$issuer:$subject:${random.joinToString("") { it.toString() }}"
    }
}

/**
 * JWT発行インターフェース
 */
interface JwtIssuer {
    fun createToken(userId: Long, issuer: String, subject: String, audience: String): String
}