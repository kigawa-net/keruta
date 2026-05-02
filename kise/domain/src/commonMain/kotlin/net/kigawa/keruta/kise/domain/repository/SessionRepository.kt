package net.kigawa.keruta.kise.domain.repository

import net.kigawa.keruta.kise.domain.entity.Session

/**
 * セッションリポジトリのポートインターフェース
 */
interface SessionRepository {
    suspend fun create(session: Session): Session
    suspend fun getByToken(token: String): Session?
    suspend fun getByUserId(userId: Long): List<Session>
    suspend fun deleteByToken(token: String)
    suspend fun deleteExpired(userId: Long)
    suspend fun countByUserId(userId: Long): Int
}