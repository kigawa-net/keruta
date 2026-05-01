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
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiresAt
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