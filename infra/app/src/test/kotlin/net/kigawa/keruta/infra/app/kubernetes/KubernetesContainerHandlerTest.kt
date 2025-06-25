package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ConfigMap
import io.fabric8.kubernetes.api.model.ConfigMapList
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation
import io.fabric8.kubernetes.client.dsl.Resource
import net.kigawa.keruta.core.domain.model.KubernetesConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class KubernetesContainerHandlerTest {

    private lateinit var kubernetesContainerHandler: KubernetesContainerHandler
    private lateinit var containerCreator: KubernetesContainerCreator
    private lateinit var scriptExecutionHandler: KubernetesScriptExecutionHandler
    private lateinit var clientProvider: KubernetesClientProvider
    private lateinit var client: KubernetesClient
    private lateinit var config: KubernetesConfig

    @BeforeEach
    fun setUp() {
        // Mock KubernetesClientProvider and its dependencies
        clientProvider = mock(KubernetesClientProvider::class.java)
        client = mock(KubernetesClient::class.java)
        config = KubernetesConfig(
            enabled = true,
            defaultNamespace = "test-namespace"
        )

        `when`(clientProvider.getClient()).thenReturn(client)
        `when`(clientProvider.getConfig()).thenReturn(config)

        // Mock the new dependencies
        containerCreator = mock(KubernetesContainerCreator::class.java)
        scriptExecutionHandler = mock(KubernetesScriptExecutionHandler::class.java)

        // Create the handler with the mocked dependencies
        kubernetesContainerHandler = KubernetesContainerHandler(containerCreator, scriptExecutionHandler)
    }

    @Test
    fun `setupScriptExecution should delegate to scriptExecutionHandler`() {
        // Given
        val container = Container()
        container.name = "test-container"
        container.image = "test-image"
        container.command = listOf("original-command")
        container.args = listOf("original-arg1", "original-arg2")

        val workVolumeName = "test-volume"
        val workMountPath = "/workspace"

        // When
        kubernetesContainerHandler.setupScriptExecution(container, workVolumeName, workMountPath)

        // Then
        // Verify that the handler delegates to scriptExecutionHandler
        verify(scriptExecutionHandler).setupScriptExecution(container, workVolumeName, workMountPath)
    }

    @Test
    fun `createMainContainer should delegate to containerCreator`() {
        // Given
        val task = mock(net.kigawa.keruta.core.domain.model.Task::class.java)
        val image = "test-image"
        val resources = mock(net.kigawa.keruta.core.domain.model.Resources::class.java)
        val additionalEnv = mapOf("key" to "value")

        // When
        kubernetesContainerHandler.createMainContainer(task, image, resources, additionalEnv)

        // Then
        // Verify that the handler delegates to containerCreator
        verify(containerCreator).createMainContainer(task, image, resources, additionalEnv)
    }
}
