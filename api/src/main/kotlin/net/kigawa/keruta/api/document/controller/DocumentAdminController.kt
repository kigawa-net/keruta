package net.kigawa.keruta.api.document.controller

import net.kigawa.keruta.core.domain.model.Document
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@Controller
@RequestMapping("/admin/documents")
class DocumentAdminController {

    private val documents = mutableListOf<Document>()

    @GetMapping
    fun documentList(model: Model): String {
        model.addAttribute("pageTitle", "Document Management")
        model.addAttribute("documents", documents)
        return "admin/documents"
    }

    @GetMapping("/create")
    fun createDocumentForm(model: Model): String {
        model.addAttribute("pageTitle", "Create Document")
        model.addAttribute("document", Document(
            title = "",
            content = "",
            tags = emptyList()
        ))
        return "admin/document-form"
    }

    @PostMapping("/create")
    fun createDocument(@ModelAttribute document: Document): String {
        val newDocument = document.copy(
            id = UUID.randomUUID().toString(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        documents.add(newDocument)
        return "redirect:/admin/documents"
    }

    @GetMapping("/edit/{id}")
    fun editDocumentForm(@PathVariable id: String, model: Model): String {
        val document = documents.find { it.id == id }
        if (document != null) {
            model.addAttribute("pageTitle", "Edit Document")
            model.addAttribute("document", document)
            return "admin/document-form"
        }
        return "redirect:/admin/documents"
    }

    @PostMapping("/edit/{id}")
    fun updateDocument(@PathVariable id: String, @ModelAttribute document: Document): String {
        val index = documents.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedDocument = document.copy(
                id = id,
                updatedAt = LocalDateTime.now()
            )
            documents[index] = updatedDocument
        }
        return "redirect:/admin/documents"
    }

    @GetMapping("/delete/{id}")
    fun deleteDocument(@PathVariable id: String): String {
        documents.removeIf { it.id == id }
        return "redirect:/admin/documents"
    }
}