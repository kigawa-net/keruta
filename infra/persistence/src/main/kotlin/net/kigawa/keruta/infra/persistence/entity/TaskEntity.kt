package net.kigawa.keruta.infra.persistence.entity

import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import net.kigawa.keruta.core.domain.model.Document as DomainDocument

@Document(collection = "tasks")
data class TaskEntity(
    @Id
    val id: String,
    val title: String,
    val description: String? = null,
    val priority: Int = 0,
    val status: String = TaskStatus.PENDING.name,
    val gitRepository: String? = null,
    val document: String? = null,
    val image: String? = null,
    val namespace: String = "default",
    val podName: String? = null,
    val cpuResource: String? = null,
    val memoryResource: String? = null,
    val additionalEnv: Map<String, String> = emptyMap(),
    val logs: String? = null,
    val agentId: String? = null,
    val parentId: String? = null,
    val storageClass: String = "",
    val pvcName: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority,
                status = task.status.name,
                gitRepository = task.repositoryId, // Repository property removed from Task model
                document = task.documents.firstOrNull()?.content,
                image = task.image,
                namespace = task.namespace,
                podName = task.podName,
                cpuResource = null, // Resources property removed from Task model
                memoryResource = null, // Resources property removed from Task model
                additionalEnv = task.additionalEnv,
                logs = task.logs,
                agentId = task.agentId,
                parentId = task.parentId,
                storageClass = task.storageClass,
                pvcName = task.pvcName,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt,
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
            // repository parameter removed from Task model
            documents = document?.let { listOf(DomainDocument(title = title, content = it)) } ?: emptyList(),
            image = image,
            namespace = namespace,
            podName = podName,
            // resources parameter removed from Task model
            additionalEnv = additionalEnv,
            logs = logs,
            agentId = agentId,
            parentId = parentId,
            storageClass = storageClass,
            pvcName = pvcName,
            createdAt = createdAt,
            updatedAt = updatedAt,
            repositoryId = gitRepository,
        )
    }
}
