package net.kigawa.keruta.core.domain.model

import java.time.LocalDateTime

/**
 * Represents a task in the system.
 * This class combines the previous Task and Job entities.
 */
data class Task(
    val id: String? = null,
    val title: String = "a",
    val description: String? = null,
    val priority: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING,
    val documents: List<Document> = emptyList(),
    val image: String? = null,
    val namespace: String = "default",
    val jobName: String? = null,
    val podName: String? = null, // Kept for backward compatibility
    val additionalEnv: Map<String, String> = emptyMap(),
    val kubernetesManifest: String? = null,
    val logs: String? = null,
    val agentId: String? = null,
    val repositoryId: String? = null, // Added for git clone in init container
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Represents the status of a task.
 * This enum combines the previous TaskStatus and JobStatus enums.
 */
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,  // Equivalent to RUNNING in the previous JobStatus
    COMPLETED,
    CANCELLED,
    FAILED
}

/**
 * Represents the resource requirements for a task.
 */
data class Resources(
    val cpu: String,
    val memory: String
)
