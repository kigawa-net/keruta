package net.kigawa.keruta.core.domain.model

import java.time.LocalDateTime

data class Task(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val priority: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING,
    val repository: Repository? = null,
    val documents: List<Document> = emptyList(),
    val image: String? = null,
    val namespace: String = "default",
    val podName: String? = null,
    val resources: Resources? = null,
    val additionalEnv: Map<String, String> = emptyMap(),
    val logs: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    FAILED
}
