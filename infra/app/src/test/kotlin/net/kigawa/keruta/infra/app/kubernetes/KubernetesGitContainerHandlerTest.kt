package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.VolumeMount
import net.kigawa.keruta.core.domain.model.Repository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class KubernetesGitContainerHandlerTest {

    private lateinit var kubernetesGitContainerHandler: KubernetesGitContainerHandler

    @BeforeEach
    fun setUp() {
        kubernetesGitContainerHandler = KubernetesGitContainerHandler()
    }

    @Test
    fun `createGitCloneContainer should create a container with correct properties`() {
        // Given
        val repository = createRepository("test-repo", "https://github.com/test/repo.git")
        val volumeName = "test-volume"
        val mountPath = "/workspace"

        // When
        val container = kubernetesGitContainerHandler.createGitCloneContainer(repository, mountPath)

        // Then
        assertEquals("git-clone", container.name)
        assertEquals("alpine/git:latest", container.image)
        assertEquals(listOf("/bin/sh", "-c"), container.command)
        
        // Verify the script contains the expected commands
        val scriptContent = container.args[0]
        assertTrue(scriptContent.contains("set -e"))
        assertTrue(scriptContent.contains("git clone --depth 1 --single-branch ${repository.url} $mountPath"))
        assertTrue(scriptContent.contains("echo '/.keruta' >> $mountPath/.git/info/exclude"))
    }

    @Test
    fun `addVolumeToMainContainer should add volume mount to container with no existing mounts`() {
        // Given
        val container = Container()
        val volumeName = "test-volume"
        val mountPath = "/workspace"

        // When
        kubernetesGitContainerHandler.addVolumeToMainContainer(volumeName, mountPath)

        // Then
        assertNotNull(container.volumeMounts)
        assertEquals(volumeName, container.volumeMounts[0].name)
        assertEquals(mountPath, container.volumeMounts[0].mountPath)
    }

    @Test
    fun `addVolumeToMainContainer should add volume mount to container with existing mounts`() {
        // Given
        val container = Container()
        val existingVolumeMount = VolumeMount()
        existingVolumeMount.name = "existing-volume"
        existingVolumeMount.mountPath = "/existing-path"
        container.volumeMounts = mutableListOf(existingVolumeMount)
        
        val volumeName = "test-volume"
        val mountPath = "/workspace"

        // When
        kubernetesGitContainerHandler.addVolumeToMainContainer(volumeName, mountPath)

        // Then
        assertNotNull(container.volumeMounts)
        assertEquals("existing-volume", container.volumeMounts[0].name)
        assertEquals("/existing-path", container.volumeMounts[0].mountPath)
        assertEquals(volumeName, container.volumeMounts[1].name)
        assertEquals(mountPath, container.volumeMounts[1].mountPath)
    }

    private fun createRepository(name: String, url: String): Repository {
        return Repository(
            id = "test-id",
            name = name,
            url = url,
            description = "Test repository",
            isValid = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
}