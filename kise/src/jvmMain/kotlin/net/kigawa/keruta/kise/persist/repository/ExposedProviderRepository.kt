package net.kigawa.keruta.kise.persist.repository

import net.kigawa.keruta.kise.domain.entity.Provider
import net.kigawa.keruta.kise.domain.repository.ProviderRepository
import net.kigawa.keruta.kise.persist.table.ProviderTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

/**
 * Exposed を使用したプロバイダーリポジトリ実装
 */
class ExposedProviderRepository : ProviderRepository {
    override suspend fun getById(id: Long): Provider? {
        val row = ProviderTable.select { ProviderTable.id eq id }.firstOrNull() ?: return null
        return rowToProvider(row)
    }

    override suspend fun getByIssuer(issuer: String): Provider? {
        val row = ProviderTable.select { ProviderTable.issuer eq issuer }.firstOrNull() ?: return null
        return rowToProvider(row)
    }

    override suspend fun getByUserId(userId: Long): List<Provider> {
        return ProviderTable.select { ProviderTable.userId eq userId }
            .map { rowToProvider(it) }
    }

    override suspend fun create(provider: Provider): Provider {
        val now = System.currentTimeMillis()
        val id = ProviderTable.insert {
            it[userId] = provider.userId
            it[name] = provider.name
            it[issuer] = provider.issuer
            it[audience] = provider.audience
            it[setting] = provider.setting
            it[createdAt] = now
        } get ProviderTable.id

        return provider.copy(
            id = id,
            createdAt = now
        )
    }

    private fun rowToProvider(row: org.jetbrains.exposed.sql.ResultRow): Provider {
        return Provider(
            id = row[ProviderTable.id],
            userId = row[ProviderTable.userId],
            name = row[ProviderTable.name],
            issuer = row[ProviderTable.issuer],
            audience = row[ProviderTable.audience],
            setting = row[ProviderTable.setting],
            createdAt = row[ProviderTable.createdAt]
        )
    }
}