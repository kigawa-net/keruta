package net.kigawa.keruta.kise.domain.entity

import kotlinx.serialization.Serializable

/**
 * システムユーザーエンティティ
 */
@Serializable
data class User(
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * ユーザーとIdPの関連付け
 */
@Serializable
data class UserIdp(
    val userId: Long,
    val providerId: Long,
    val issuer: String,
    val subject: String,
    val audience: String,
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * ユーザーIDの識別子（issuer:subject形式）
 */
@Serializable
data class UserIdentity(
    val issuer: String,
    val subject: String,
) {
    companion object {
        fun fromUserIdp(userIdp: UserIdp) = UserIdentity(userIdp.issuer, userIdp.subject)
    }

    override fun toString(): String = "$issuer:$subject"
}