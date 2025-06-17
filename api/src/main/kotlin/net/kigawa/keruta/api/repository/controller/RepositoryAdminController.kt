package net.kigawa.keruta.api.repository.controller

import net.kigawa.keruta.core.domain.model.Repository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@Controller
@RequestMapping("/admin/repositories")
class RepositoryAdminController {

    private val repositories = mutableListOf<Repository>()

    @GetMapping
    fun repositoryList(model: Model): String {
        model.addAttribute("pageTitle", "Repository Management")
        model.addAttribute("repositories", repositories)
        return "admin/repositories"
    }

    @GetMapping("/create")
    fun createRepositoryForm(model: Model): String {
        model.addAttribute("pageTitle", "Create Repository")
        model.addAttribute("repository", Repository(
            name = "",
            url = "",
            description = ""
        ))
        return "admin/repository-form"
    }

    @PostMapping("/create")
    fun createRepository(@ModelAttribute repository: Repository): String {
        val newRepository = repository.copy(
            id = UUID.randomUUID().toString(),
            isValid = validateUrl(repository.url),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        repositories.add(newRepository)
        return "redirect:/admin/repositories"
    }

    @GetMapping("/edit/{id}")
    fun editRepositoryForm(@PathVariable id: String, model: Model): String {
        val repository = repositories.find { it.id == id }
        if (repository != null) {
            model.addAttribute("pageTitle", "Edit Repository")
            model.addAttribute("repository", repository)
            return "admin/repository-form"
        }
        return "redirect:/admin/repositories"
    }

    @PostMapping("/edit/{id}")
    fun updateRepository(@PathVariable id: String, @ModelAttribute repository: Repository): String {
        val index = repositories.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedRepository = repository.copy(
                id = id,
                isValid = validateUrl(repository.url),
                updatedAt = LocalDateTime.now()
            )
            repositories[index] = updatedRepository
        }
        return "redirect:/admin/repositories"
    }

    @GetMapping("/delete/{id}")
    fun deleteRepository(@PathVariable id: String): String {
        repositories.removeIf { it.id == id }
        return "redirect:/admin/repositories"
    }

    private fun validateUrl(url: String): Boolean {
        return try {
            val uri = java.net.URI(url)
            val connection = uri.toURL().openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            responseCode in 200..299
        } catch (e: Exception) {
            false
        }
    }
}
