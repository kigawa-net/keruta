/**
 * Tests for the InMemoryGitRepositoryRepository class.
 */
package net.kigawa.keruta.infra.repositorynoapi.repository

import net.kigawa.keruta.core.domain.model.Repository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class InMemoryGitRepositoryRepositoryTest {
    
    private lateinit var repository: InMemoryGitRepositoryRepository
    
    @BeforeEach
    fun setUp() {
        repository = InMemoryGitRepositoryRepository()
    }
    
    @Test
    fun testSaveAndFindById() {
        // Given
        val repo = Repository(
            name = "Test Repo",
            url = "https://github.com/test/repo",
            description = "Test repository"
        )
        
        // When
        val savedRepo = repository.save(repo)
        val foundRepo = repository.findById(savedRepo.id!!)
        
        // Then
        assertNotNull(savedRepo.id)
        assertEquals(savedRepo, foundRepo)
    }
    
    @Test
    fun testFindAll() {
        // Given
        val repo1 = Repository(name = "Repo 1", url = "https://github.com/test/repo1")
        val repo2 = Repository(name = "Repo 2", url = "https://github.com/test/repo2")
        
        // When
        repository.save(repo1)
        repository.save(repo2)
        val allRepos = repository.findAll()
        
        // Then
        assertEquals(2, allRepos.size)
        assertTrue(allRepos.any { it.name == "Repo 1" })
        assertTrue(allRepos.any { it.name == "Repo 2" })
    }
    
    @Test
    fun testDeleteById() {
        // Given
        val repo = Repository(name = "Test Repo", url = "https://github.com/test/repo")
        val savedRepo = repository.save(repo)
        
        // When
        val deleteResult = repository.deleteById(savedRepo.id!!)
        val foundRepo = repository.findById(savedRepo.id!!)
        
        // Then
        assertTrue(deleteResult)
        assertNull(foundRepo)
    }
    
    @Test
    fun testFindByName() {
        // Given
        val repo1 = Repository(name = "Test Repo", url = "https://github.com/test/repo1")
        val repo2 = Repository(name = "Another Repo", url = "https://github.com/test/repo2")
        
        // When
        repository.save(repo1)
        repository.save(repo2)
        val foundRepos = repository.findByName("test")
        
        // Then
        assertEquals(1, foundRepos.size)
        assertEquals("Test Repo", foundRepos[0].name)
    }
}