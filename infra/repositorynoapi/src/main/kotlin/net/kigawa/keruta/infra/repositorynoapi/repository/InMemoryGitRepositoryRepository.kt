/**
 * In-memory implementation of the GitRepositoryRepository interface.
 * This implementation stores repositories in memory and doesn't depend on external services.
 */
package net.kigawa.keruta.infra.repositorynoapi.repository

import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.usecase.repository.GitRepositoryRepository
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryGitRepositoryRepository : GitRepositoryRepository {
    
    private val repositories = ConcurrentHashMap<String, Repository>()
    
    override fun findAll(): List<Repository> {
        return repositories.values.toList()
    }
    
    override fun findById(id: String): Repository? {
        return repositories[id]
    }
    
    override fun save(repository: Repository): Repository {
        val id = repository.id ?: UUID.randomUUID().toString()
        val updatedRepository = repository.copy(
            id = id,
            updatedAt = LocalDateTime.now(),
            createdAt = repositories[id]?.createdAt ?: repository.createdAt
        )
        repositories[id] = updatedRepository
        return updatedRepository
    }
    
    override fun deleteById(id: String): Boolean {
        return if (repositories.containsKey(id)) {
            repositories.remove(id)
            true
        } else {
            false
        }
    }
    
    override fun validateUrl(url: String): Boolean {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            responseCode in 200..299
        } catch (e: Exception) {
            false
        }
    }
    
    override fun findByName(name: String): List<Repository> {
        return repositories.values.filter { 
            it.name.contains(name, ignoreCase = true) 
        }
    }
}