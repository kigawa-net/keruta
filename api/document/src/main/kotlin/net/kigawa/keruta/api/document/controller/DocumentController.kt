package net.kigawa.keruta.api.document.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import net.kigawa.keruta.core.domain.model.Document
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Document", description = "Document management API")
class DocumentController {

    private val documents = mutableListOf<Document>()

    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieves a list of all documents")
    fun getAllDocuments(): List<Document> {
        return documents
    }

    @PostMapping
    @Operation(summary = "Create document", description = "Creates a new document")
    fun createDocument(@RequestBody document: Document): Document {
        val newDocument = document.copy(
            id = UUID.randomUUID().toString(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        documents.add(newDocument)
        return newDocument
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieves a specific document by its ID")
    fun getDocumentById(@PathVariable id: String): ResponseEntity<Document> {
        val document = documents.find { it.id == id }
        return if (document != null) {
            ResponseEntity.ok(document)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update document", description = "Updates an existing document")
    fun updateDocument(@PathVariable id: String, @RequestBody document: Document): ResponseEntity<Document> {
        val index = documents.indexOfFirst { it.id == id }
        if (index == -1) {
            return ResponseEntity.notFound().build()
        }

        val updatedDocument = document.copy(
            id = id,
            updatedAt = LocalDateTime.now()
        )
        documents[index] = updatedDocument
        return ResponseEntity.ok(updatedDocument)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Deletes a document")
    fun deleteDocument(@PathVariable id: String): ResponseEntity<Void> {
        val removed = documents.removeIf { it.id == id }
        return if (removed) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search documents", description = "Searches for documents based on query parameters")
    fun searchDocuments(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) tag: String?
    ): List<Document> {
        return documents.filter { document ->
            (query == null || document.title.contains(query, ignoreCase = true) || document.content.contains(query, ignoreCase = true)) &&
            (tag == null || document.tags.any { it.equals(tag, ignoreCase = true) })
        }
    }
}