package net.kigawa.keruta.api.task.dto

/**
 * Request DTO for creating a job.
 */
data class CreateJobRequest(
    val image: String,
    val namespace: String? = null,
    val podName: String? = null,
    val resources: ResourcesDto? = null,
    val additionalEnv: Map<String, String>? = null
)

/**
 * DTO for resource requirements.
 */
data class ResourcesDto(
    val cpu: String,
    val memory: String
)