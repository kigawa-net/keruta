package net.kigawa.keruta.infra.persistence.entity

import net.kigawa.keruta.core.domain.model.Document as DomainDocument
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "tasks")
data class TaskEntity(
    @Id
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val priority: Int = 0,
    val status: String = TaskStatus.PENDING.name,
    val gitRepository: String? = null,
    val document: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority,
                status = task.status.name,
                gitRepository = task.repository?.url,
                document = task.documents.firstOrNull()?.content,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt
            )
        }
    }

    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            priority = priority,
            status = TaskStatus.valueOf(status),
            repository = gitRepository?.let { Repository(url = it, name = it.substringAfterLast('/')) },
            documents = document?.let { listOf(DomainDocument(title = title, content = it)) } ?: emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}