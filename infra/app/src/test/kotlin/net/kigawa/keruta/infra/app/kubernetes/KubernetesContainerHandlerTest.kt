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

        // Create the handler with the mocked dependencies
        kubernetesContainerHandler = KubernetesContainerHandler(clientProvider)
    }

    @Test
    fun `setupScriptExecution should set up volume mount, environment variables, and script command`() {
        // Given
        val container = Container()
        container.name = "test-container"
        container.image = "test-image"
        container.command = listOf("original-command")
        container.args = listOf("original-arg1", "original-arg2")

        val workVolumeName = "test-volume"
        val workMountPath = "/workspace"

        // Mock ConfigMap operations
        val configMapOperation = mock(MixedOperation::class.java) as MixedOperation<ConfigMap, ConfigMapList, Resource<ConfigMap>>
        val namespaceOperation = mock(NonNamespaceOperation::class.java) as NonNamespaceOperation<ConfigMap, ConfigMapList, Resource<ConfigMap>>
        val resource = mock(Resource::class.java) as Resource<ConfigMap>

        `when`(client.configMaps()).thenReturn(configMapOperation)
        `when`(configMapOperation.inNamespace(anyString())).thenReturn(namespaceOperation)
        `when`(namespaceOperation.withName(anyString())).thenReturn(resource)
        `when`(resource.get()).thenReturn(null) // ConfigMap doesn't exist

        // When
        kubernetesContainerHandler.setupScriptExecution(container, workVolumeName, workMountPath)

        // Then
        // Verify volume mount is set up
        assertNotNull(container.volumeMounts)
        assertEquals(1, container.volumeMounts.size)
        assertEquals(workVolumeName, container.volumeMounts[0].name)
        assertEquals(workMountPath, container.volumeMounts[0].mountPath)

        // Verify environment variables are set up
        assertNotNull(container.env)
        assertTrue(container.env.size >= 3) // At least KERUTA_REPOSITORY_ID, KERUTA_DOCUMENT_ID, and KERUTA_API_ENDPOINT

        // Find specific environment variables
        val repoIdEnv = container.env.find { it.name == "KERUTA_REPOSITORY_ID" }
        val docIdEnv = container.env.find { it.name == "KERUTA_DOCUMENT_ID" }
        val apiEndpointEnv = container.env.find { it.name == "KERUTA_API_ENDPOINT" }

        assertNotNull(repoIdEnv)
        assertNotNull(docIdEnv)
        assertNotNull(apiEndpointEnv)
        assertEquals("", repoIdEnv?.value) // Empty string as default value
        assertEquals("", docIdEnv?.value) // Empty string as default value
        assertEquals("http://keruta-api.keruta.svc.cluster.local", apiEndpointEnv?.value)

        // Verify script and command are set up
        assertEquals(listOf("/bin/sh", "-c"), container.command)
        assertEquals(1, container.args.size)

        // Verify the script contains the expected commands
        val scriptContent = container.args[0]
        assertTrue(scriptContent.contains("#!/bin/sh"))
        assertTrue(scriptContent.contains("set -e"))
        assertTrue(scriptContent.contains("mkdir -p ./.keruta"))
        assertTrue(scriptContent.contains("KERUTA_REPOSITORY_ID"))
        assertTrue(scriptContent.contains("KERUTA_DOCUMENT_ID"))
        assertTrue(scriptContent.contains("exec original-command original-arg1 original-arg2"))
    }

    @Test
    fun `setupScriptExecution should handle container with no existing command`() {
        // Given
        val container = Container()
        container.name = "test-container"
        container.image = "test-image"
        // No command or args set

        val workVolumeName = "test-volume"
        val workMountPath = "/workspace"

        // Mock ConfigMap operations
        val configMapOperation = mock(MixedOperation::class.java) as MixedOperation<ConfigMap, ConfigMapList, Resource<ConfigMap>>
        val namespaceOperation = mock(NonNamespaceOperation::class.java) as NonNamespaceOperation<ConfigMap, ConfigMapList, Resource<ConfigMap>>
        val resource = mock(Resource::class.java) as Resource<ConfigMap>

        `when`(client.configMaps()).thenReturn(configMapOperation)
        `when`(configMapOperation.inNamespace(anyString())).thenReturn(namespaceOperation)
        `when`(namespaceOperation.withName(anyString())).thenReturn(resource)
        `when`(resource.get()).thenReturn(null) // ConfigMap doesn't exist

        // When
        kubernetesContainerHandler.setupScriptExecution(container, workVolumeName, workMountPath)

        // Then
        // Verify script and command are set up
        assertEquals(listOf("/bin/sh", "-c"), container.command)
        assertEquals(1, container.args.size)

        // Verify the script contains the expected commands
        val scriptContent = container.args[0]
        assertTrue(scriptContent.contains("#!/bin/sh"))
        assertTrue(scriptContent.contains("set -e"))
        assertTrue(scriptContent.contains("mkdir -p ./.keruta"))
        assertTrue(scriptContent.contains("KERUTA_REPOSITORY_ID"))
        assertTrue(scriptContent.contains("KERUTA_DOCUMENT_ID"))
    }
}
