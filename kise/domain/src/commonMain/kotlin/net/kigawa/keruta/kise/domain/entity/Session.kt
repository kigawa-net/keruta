package net.kigawa.keruta.kise.domain.entity

import kotlinx.serialization.Serializable

/**
 * アクティブセッション
 */
@Serializable
data class Session(
    val id: Long = 0,
    val userId: Long,
    val token: String,
    val expiresAt: Long,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
) {
    // Note: isExpired() はプラットフォーム固有の実装が必要
    // commonMainでは比較のみ可能
    fun isExpired(currentTime: Long): Boolean = currentTime > expiresAt
}

/**
 * セッションのステータス
 */
enum class SessionStatus {
    ACTIVE,
    EXPIRED,
    REVOKED,
}

/**
 * 認証結果
 */
@Serializable
data class AuthResult(
    val user: User,
    val userIdp: UserIdp,
    val session: Session,
)