/**
 * Represents a Git repository in the system.
 *
 * @property id The unique identifier of the repository
 * @property name The name of the repository
 * @property url The URL of the Git repository
 * @property description A description of the repository
 * @property isValid Whether the repository URL is valid and accessible
 * @property createdAt The timestamp when the repository was added to the system
 * @property updatedAt The timestamp when the repository was last updated
 */
package net.kigawa.keruta.core.domain.model

import java.time.LocalDateTime

data class Repository(
    val id: String? = null,
    val name: String,
    val url: String,
    val description: String = "",
    val isValid: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)