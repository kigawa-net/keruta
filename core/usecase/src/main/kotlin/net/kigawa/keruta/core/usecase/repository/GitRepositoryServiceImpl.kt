/**
 * Implementation of the GitRepositoryService interface.
 */
package net.kigawa.keruta.core.usecase.repository

import net.kigawa.keruta.core.domain.model.Repository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GitRepositoryServiceImpl(private val gitRepositoryRepository: GitRepositoryRepository) : GitRepositoryService {
    
    override fun getAllRepositories(): List<Repository> {
        return gitRepositoryRepository.findAll()
    }
    
    override fun getRepositoryById(id: String): Repository {
        return gitRepositoryRepository.findById(id) ?: throw NoSuchElementException("Repository not found with id: $id")
    }
    
    override fun createRepository(repository: Repository): Repository {
        val validatedRepository = repository.copy(
            isValid = validateRepositoryUrl(repository.url)
        )
        return gitRepositoryRepository.save(validatedRepository)
    }
    
    override fun updateRepository(id: String, repository: Repository): Repository {
        val existingRepository = getRepositoryById(id)
        val updatedRepository = repository.copy(
            id = existingRepository.id,
            isValid = validateRepositoryUrl(repository.url),
            createdAt = existingRepository.createdAt,
            updatedAt = LocalDateTime.now()
        )
        return gitRepositoryRepository.save(updatedRepository)
    }
    
    override fun deleteRepository(id: String) {
        if (!gitRepositoryRepository.deleteById(id)) {
            throw NoSuchElementException("Repository not found with id: $id")
        }
    }
    
    override fun validateRepositoryUrl(url: String): Boolean {
        return gitRepositoryRepository.validateUrl(url)
    }
    
    override fun getRepositoriesByName(name: String): List<Repository> {
        return gitRepositoryRepository.findByName(name)
    }
}