package net.kigawa.keruta.core.usecase.task.background

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for the background task processor.
 */
@ConfigurationProperties(prefix = "keruta.task.processor")
data class BackgroundTaskProcessorConfig(
    /**
     * The Docker image to use for tasks.
     */
    val defaultImage: String = "keruta-task-executor:latest",

    /**
     * The Kubernetes namespace to use for tasks.
     */
    val defaultNamespace: String = "default",

    /**
     * The delay between task processing attempts in milliseconds.
     */
    val processingDelay: Long = 5000
)