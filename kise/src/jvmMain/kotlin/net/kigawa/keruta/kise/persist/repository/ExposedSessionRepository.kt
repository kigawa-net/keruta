package net.kigawa.keruta.kise.persist.repository

import net.kigawa.keruta.kise.domain.entity.Session
import net.kigawa.keruta.kise.domain.repository.SessionRepository
import net.kigawa.keruta.kise.persist.table.SessionTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

/**
 * Exposed を使用したセッションリポジトリ実装
 */
class ExposedSessionRepository : SessionRepository {
    override suspend fun create(session: Session): Session {
        val now = System.currentTimeMillis()
        val id = SessionTable.insert {
            it[userId] = session.userId
            it[token] = session.token
            it[expiresAt] = session.expiresAt
            it[createdAt] = now
            it[updatedAt] = now
        } get SessionTable.id

        return session.copy(
            id = id,
            createdAt = now,
            updatedAt = now
        )
    }

    override suspend fun getByToken(token: String): Session? {
        val row = SessionTable
            .selectAll()
            .where { SessionTable.token eq token }
            .firstOrNull() ?: return null

        return Session(
            id = row[SessionTable.id],
            userId = row[SessionTable.userId],
            token = row[SessionTable.token],
            expiresAt = row[SessionTable.expiresAt],
            createdAt = row[SessionTable.createdAt],
            updatedAt = row[SessionTable.updatedAt]
        )
    }

    override suspend fun getByUserId(userId: Long): List<Session> {
        return SessionTable
            .selectAll()
            .where { SessionTable.userId eq userId }
            .map { row ->
                Session(
                    id = row[SessionTable.id],
                    userId = row[SessionTable.userId],
                    token = row[SessionTable.token],
                    expiresAt = row[SessionTable.expiresAt],
                    createdAt = row[SessionTable.createdAt],
                    updatedAt = row[SessionTable.updatedAt]
                )
            }
    }

    override suspend fun deleteByToken(token: String) {
        SessionTable.deleteWhere { SessionTable.token eq token }
    }

    override suspend fun deleteExpired(userId: Long) {
        val now = System.currentTimeMillis()
        SessionTable.deleteWhere {
            (SessionTable.userId eq userId) and (SessionTable.expiresAt less now)
        }
    }

    override suspend fun countByUserId(userId: Long): Int {
        return SessionTable
            .selectAll()
            .where { SessionTable.userId eq userId }
            .count()
            .toInt()
    }
}