package net.kigawa.keruta.kise.persist.repository

import net.kigawa.keruta.kise.domain.entity.User
import net.kigawa.keruta.kise.domain.entity.UserIdp
import net.kigawa.keruta.kise.domain.repository.UserRepository
import net.kigawa.keruta.kise.persist.table.UserIdpTable
import net.kigawa.keruta.kise.persist.table.UserTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

/**
 * Exposed を使用したユーザーリポジトリ実装
 */
class ExposedUserRepository : UserRepository {
    override suspend fun create(): User {
        val now = System.currentTimeMillis()
        val id = UserTable.insert {
            it[createdAt] = now
        } get UserTable.id
        return User(id = id, createdAt = now)
    }

    override suspend fun getById(id: Long): User? {
        val row = UserTable.select { UserTable.id eq id }.firstOrNull() ?: return null
        return User(
            id = row[UserTable.id],
            createdAt = row[UserTable.createdAt]
        )
    }

    override suspend fun getUserIdp(userId: Long, issuer: String, subject: String): UserIdp? {
        val row = UserIdpTable.select {
            (UserIdpTable.userId eq userId) and
                (UserIdpTable.issuer eq issuer) and
                (UserIdpTable.subject eq subject)
        }.firstOrNull() ?: return null

        return UserIdp(
            userId = row[UserIdpTable.userId],
            providerId = row[UserIdpTable.providerId],
            issuer = row[UserIdpTable.issuer],
            subject = row[UserIdpTable.subject],
            audience = row[UserIdpTable.audience],
            createdAt = row[UserIdpTable.createdAt]
        )
    }

    override suspend fun getUserIdpByIdentity(issuer: String, subject: String): UserIdp? {
        val row = UserIdpTable.select {
            (UserIdpTable.issuer eq issuer) and (UserIdpTable.subject eq subject)
        }.firstOrNull() ?: return null

        return UserIdp(
            userId = row[UserIdpTable.userId],
            providerId = row[UserIdpTable.providerId],
            issuer = row[UserIdpTable.issuer],
            subject = row[UserIdpTable.subject],
            audience = row[UserIdpTable.audience],
            createdAt = row[UserIdpTable.createdAt]
        )
    }

    override suspend fun createUserIdp(userIdp: UserIdp): UserIdp {
        val now = System.currentTimeMillis()
        UserIdpTable.insert {
            it[userId] = userIdp.userId
            it[providerId] = userIdp.providerId
            it[issuer] = userIdp.issuer
            it[subject] = userIdp.subject
            it[audience] = userIdp.audience
            it[createdAt] = now
        }
        return userIdp.copy(createdAt = now)
    }
}