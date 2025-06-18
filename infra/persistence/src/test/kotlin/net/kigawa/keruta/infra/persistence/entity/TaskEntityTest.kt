package net.kigawa.keruta.infra.persistence.entity

import net.kigawa.keruta.core.domain.model.Document
import net.kigawa.keruta.core.domain.model.Repository
import net.kigawa.keruta.core.domain.model.Resources
import net.kigawa.keruta.core.domain.model.Task
import net.kigawa.keruta.core.domain.model.TaskStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TaskEntityTest {

    @Test
    fun `fromDomain should correctly map Task to TaskEntity`() {
        // Given
        val now = LocalDateTime.now()
        val task = Task(
            id = "123",
            title = "Test Task",
            description = "Test Description",
            priority = 1,
            status = TaskStatus.IN_PROGRESS,
            repository = Repository(
                url = "https://github.com/kigawa-net/keruta",
                name = "keruta"
            ),
            documents = listOf(
                Document(
                    title = "Test Document",
                    content = "Test Content"
                )
            ),
            image = "test-image",
            namespace = "test-namespace",
            podName = "test-pod",
            resources = Resources(
                cpu = "100m",
                memory = "128Mi"
            ),
            additionalEnv = mapOf("KEY" to "VALUE"),
            logs = "Test logs",
            createdAt = now,
            updatedAt = now
        )

        // When
        val taskEntity = TaskEntity.fromDomain(task)

        // Then
        assertEquals("123", taskEntity.id)
        assertEquals("Test Task", taskEntity.title)
        assertEquals("Test Description", taskEntity.description)
        assertEquals(1, taskEntity.priority)
        assertEquals(TaskStatus.IN_PROGRESS.name, taskEntity.status)
        assertEquals("https://github.com/kigawa-net/keruta", taskEntity.gitRepository)
        assertEquals("Test Content", taskEntity.document)
        assertEquals("test-image", taskEntity.image)
        assertEquals("test-namespace", taskEntity.namespace)
        assertEquals("test-pod", taskEntity.podName)
        assertEquals("100m", taskEntity.cpuResource)
        assertEquals("128Mi", taskEntity.memoryResource)
        assertEquals(mapOf("KEY" to "VALUE"), taskEntity.additionalEnv)
        assertEquals("Test logs", taskEntity.logs)
        assertEquals(now, taskEntity.createdAt)
        assertEquals(now, taskEntity.updatedAt)
    }

    @Test
    fun `toDomain should correctly map TaskEntity to Task`() {
        // Given
        val now = LocalDateTime.now()
        val taskEntity = TaskEntity(
            id = "123",
            title = "Test Task",
            description = "Test Description",
            priority = 1,
            status = TaskStatus.IN_PROGRESS.name,
            gitRepository = "https://github.com/kigawa-net/keruta",
            document = "Test Content",
            image = "test-image",
            namespace = "test-namespace",
            podName = "test-pod",
            cpuResource = "100m",
            memoryResource = "128Mi",
            additionalEnv = mapOf("KEY" to "VALUE"),
            logs = "Test logs",
            createdAt = now,
            updatedAt = now
        )

        // When
        val task = taskEntity.toDomain()

        // Then
        assertEquals("123", task.id)
        assertEquals("Test Task", task.title)
        assertEquals("Test Description", task.description)
        assertEquals(1, task.priority)
        assertEquals(TaskStatus.IN_PROGRESS, task.status)
        assertEquals("https://github.com/kigawa-net/keruta", task.repository?.url)
        assertEquals("keruta", task.repository?.name)
        assertEquals(1, task.documents.size)
        assertEquals("Test Task", task.documents[0].title)
        assertEquals("Test Content", task.documents[0].content)
        assertEquals("test-image", task.image)
        assertEquals("test-namespace", task.namespace)
        assertEquals("test-pod", task.podName)
        assertEquals("100m", task.resources?.cpu)
        assertEquals("128Mi", task.resources?.memory)
        assertEquals(mapOf("KEY" to "VALUE"), task.additionalEnv)
        assertEquals("Test logs", task.logs)
        assertEquals(now, task.createdAt)
        assertEquals(now, task.updatedAt)
    }

    @Test
    fun `toDomain should handle null values correctly`() {
        // Given
        val now = LocalDateTime.now()
        val taskEntity = TaskEntity(
            id = "123",
            title = "Test Task",
            description = null,
            priority = 1,
            status = TaskStatus.PENDING.name,
            gitRepository = null,
            document = null,
            image = null,
            namespace = "default",
            podName = null,
            cpuResource = null,
            memoryResource = null,
            additionalEnv = emptyMap(),
            logs = null,
            createdAt = now,
            updatedAt = now
        )

        // When
        val task = taskEntity.toDomain()

        // Then
        assertEquals("123", task.id)
        assertEquals("Test Task", task.title)
        assertEquals(null, task.description)
        assertEquals(1, task.priority)
        assertEquals(TaskStatus.PENDING, task.status)
        assertEquals(null, task.repository)
        assertEquals(emptyList<Document>(), task.documents)
        assertEquals(null, task.image)
        assertEquals("default", task.namespace)
        assertEquals(null, task.podName)
        assertEquals(null, task.resources)
        assertEquals(emptyMap<String, String>(), task.additionalEnv)
        assertEquals(null, task.logs)
        assertEquals(now, task.createdAt)
        assertEquals(now, task.updatedAt)
    }

    @Test
    fun `fromDomain and toDomain should be reversible`() {
        // Given
        val now = LocalDateTime.now()
        val originalTask = Task(
            id = "123",
            title = "Test Task",
            description = "Test Description",
            priority = 1,
            status = TaskStatus.IN_PROGRESS,
            repository = Repository(
                url = "https://github.com/kigawa-net/keruta",
                name = "keruta"
            ),
            documents = listOf(
                Document(
                    title = "Test Document",
                    content = "Test Content"
                )
            ),
            image = "test-image",
            namespace = "test-namespace",
            podName = "test-pod",
            resources = Resources(
                cpu = "100m",
                memory = "128Mi"
            ),
            additionalEnv = mapOf("KEY" to "VALUE"),
            logs = "Test logs",
            createdAt = now,
            updatedAt = now
        )

        // When
        val taskEntity = TaskEntity.fromDomain(originalTask)
        val convertedTask = taskEntity.toDomain()

        // Then
        assertEquals(originalTask.id, convertedTask.id)
        assertEquals(originalTask.title, convertedTask.title)
        assertEquals(originalTask.description, convertedTask.description)
        assertEquals(originalTask.priority, convertedTask.priority)
        assertEquals(originalTask.status, convertedTask.status)
        assertEquals(originalTask.repository?.url, convertedTask.repository?.url)
        // Note: The name is derived from the URL in toDomain, so it might be different
        assertEquals(originalTask.image, convertedTask.image)
        assertEquals(originalTask.namespace, convertedTask.namespace)
        assertEquals(originalTask.podName, convertedTask.podName)
        assertEquals(originalTask.resources?.cpu, convertedTask.resources?.cpu)
        assertEquals(originalTask.resources?.memory, convertedTask.resources?.memory)
        assertEquals(originalTask.additionalEnv, convertedTask.additionalEnv)
        assertEquals(originalTask.logs, convertedTask.logs)
        assertEquals(originalTask.createdAt, convertedTask.createdAt)
        assertEquals(originalTask.updatedAt, convertedTask.updatedAt)
    }
}