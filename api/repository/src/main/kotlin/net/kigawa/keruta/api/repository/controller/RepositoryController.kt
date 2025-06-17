package net.kigawa.keruta.api.repository.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.core.domain.model.Repository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/v1/repositories")
@Tag(name = "Repository", description = "Git repository management API")
class RepositoryController {

    private val repositories = mutableListOf<Repository>()

    @GetMapping
    @Operation(summary = "Get all repositories", description = "Retrieves a list of all Git repositories")
    fun getAllRepositories(): List<Repository> {
        return repositories
    }

    @PostMapping
    @Operation(summary = "Create repository", description = "Registers a new Git repository URL")
    fun createRepository(@RequestBody repository: Repository): Repository {
        val newRepository = repository.copy(
            id = UUID.randomUUID().toString(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        repositories.add(newRepository)
        return newRepository
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get repository by ID", description = "Retrieves a specific Git repository by its ID")
    fun getRepositoryById(@PathVariable id: String): ResponseEntity<Repository> {
        val repository = repositories.find { it.id == id }
        return if (repository != null) {
            ResponseEntity.ok(repository)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update repository", description = "Updates an existing Git repository information")
    fun updateRepository(@PathVariable id: String, @RequestBody repository: Repository): ResponseEntity<Repository> {
        val index = repositories.indexOfFirst { it.id == id }
        if (index == -1) {
            return ResponseEntity.notFound().build()
        }

        val updatedRepository = repository.copy(
            id = id,
            updatedAt = LocalDateTime.now()
        )
        repositories[index] = updatedRepository
        return ResponseEntity.ok(updatedRepository)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete repository", description = "Deletes a Git repository")
    fun deleteRepository(@PathVariable id: String): ResponseEntity<Void> {
        val removed = repositories.removeIf { it.id == id }
        return if (removed) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate repository URL", description = "Validates if the Git repository URL is accessible")
    fun validateRepository(@PathVariable id: String): ResponseEntity<Map<String, Boolean>> {
        val repository = repositories.find { it.id == id }
        if (repository == null) {
            return ResponseEntity.notFound().build()
        }

        // In a real implementation, this would actually try to connect to the Git repository
        // For now, we'll just simulate validation by checking if the URL starts with a valid protocol
        val isValid = repository.url.startsWith("http://") || repository.url.startsWith("https://") || 
                      repository.url.startsWith("git://") || repository.url.startsWith("ssh://")
        
        // Update the repository with the validation result
        val index = repositories.indexOfFirst { it.id == id }
        repositories[index] = repository.copy(isValid = isValid, updatedAt = LocalDateTime.now())
        
        return ResponseEntity.ok(mapOf("valid" to isValid))
    }
}