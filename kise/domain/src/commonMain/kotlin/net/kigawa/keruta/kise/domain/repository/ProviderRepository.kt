package net.kigawa.keruta.kise.domain.repository

import net.kigawa.keruta.kise.domain.entity.Provider

/**
 * プロバイダーリポジトリのポートインターフェース
 */
interface ProviderRepository {
    suspend fun getById(id: Long): Provider?
    suspend fun getByIssuer(issuer: String): Provider?
    suspend fun getByUserId(userId: Long): List<Provider>
    suspend fun create(provider: Provider): Provider
}
