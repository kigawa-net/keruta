package net.kigawa.keruta.kise.domain.repository

import net.kigawa.keruta.kise.domain.entity.User
import net.kigawa.keruta.kise.domain.entity.UserIdp

/**
 * ユーザーリポジトリのポートインターフェース
 */
interface UserRepository {
    suspend fun create(): User
    suspend fun getById(id: Long): User?
    suspend fun getUserIdp(userId: Long, issuer: String, subject: String): UserIdp?
    suspend fun getUserIdpByIdentity(issuer: String, subject: String): UserIdp?
    suspend fun createUserIdp(userIdp: UserIdp): UserIdp
}