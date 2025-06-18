package net.kigawa.keruta.api.repository.controller

import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.usecase.repository.GitRepositoryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/repositories")
class RepositoryAdminController(
    private val gitRepositoryService: GitRepositoryService
) {

    @GetMapping
    fun repositoryList(model: Model): String {
        model.addAttribute("pageTitle", "Repository Management")
        model.addAttribute("repositories", gitRepositoryService.getAllRepositories())
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
        gitRepositoryService.createRepository(repository)
        return "redirect:/admin/repositories"
    }

    @GetMapping("/edit/{id}")
    fun editRepositoryForm(@PathVariable id: String, model: Model): String {
        try {
            val repository = gitRepositoryService.getRepositoryById(id)
            model.addAttribute("pageTitle", "Edit Repository")
            model.addAttribute("repository", repository)
            return "admin/repository-form"
        } catch (e: NoSuchElementException) {
            return "redirect:/admin/repositories"
        }
    }

    @PostMapping("/edit/{id}")
    fun updateRepository(@PathVariable id: String, @ModelAttribute repository: Repository): String {
        try {
            gitRepositoryService.updateRepository(id, repository)
        } catch (e: NoSuchElementException) {
            // Repository not found, ignore
        }
        return "redirect:/admin/repositories"
    }

    @GetMapping("/delete/{id}")
    fun deleteRepository(@PathVariable id: String): String {
        try {
            gitRepositoryService.deleteRepository(id)
        } catch (e: NoSuchElementException) {
            // Repository not found, ignore
        }
        return "redirect:/admin/repositories"
    }
}
