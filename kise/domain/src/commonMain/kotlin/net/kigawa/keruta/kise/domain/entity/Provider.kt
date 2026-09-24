package net.kigawa.keruta.kise.domain.entity

import kotlinx.serialization.Serializable

/**
 * 認証プロバイダーエンティティ
 */
@Serializable
data class Provider(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val issuer: String,
    val audience: String,
    val setting: String = "{}",
    val createdAt: Long = 0,
)

/**
 * プロバイダーの設定
 */
@Serializable
data class ProviderConfig(
    val jwksUrl: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null,
)
