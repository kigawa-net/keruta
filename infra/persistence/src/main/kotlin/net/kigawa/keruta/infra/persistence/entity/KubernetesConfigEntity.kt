package net.kigawa.keruta.infra.persistence.entity

import net.kigawa.keruta.core.domain.model.KubernetesConfig
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * MongoDB entity for Kubernetes configuration.
 */
@Document(collection = "kubernetes_config")
data class KubernetesConfigEntity(
    @Id
    val id: String? = null,
    val enabled: Boolean = false,
    val configPath: String = "",
    val inCluster: Boolean = false,
    val defaultNamespace: String = "default",
    val defaultImage: String = "keruta-task-executor:latest",
    val processorNamespace: String = "default",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromDomain(config: KubernetesConfig): KubernetesConfigEntity {
            return KubernetesConfigEntity(
                id = config.id,
                enabled = config.enabled,
                configPath = config.configPath,
                inCluster = config.inCluster,
                defaultNamespace = config.defaultNamespace,
                defaultImage = config.defaultImage,
                processorNamespace = config.processorNamespace,
                createdAt = config.createdAt,
                updatedAt = config.updatedAt
            )
        }
    }

    fun toDomain(): KubernetesConfig {
        return KubernetesConfig(
            id = id,
            enabled = enabled,
            configPath = configPath,
            inCluster = inCluster,
            defaultNamespace = defaultNamespace,
            defaultImage = defaultImage,
            processorNamespace = processorNamespace,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}