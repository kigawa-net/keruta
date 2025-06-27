package net.kigawa.keruta.infra.app.kubernetes

import io.fabric8.kubernetes.api.model.EnvVar
import net.kigawa.keruta.core.usecase.agent.KerutaAgentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension::class)
class KubernetesEnvironmentHandlerTest {

    @Mock
    private lateinit var kerutaAgentService: KerutaAgentService

    private lateinit var environmentHandler: KubernetesEnvironmentHandler

    @BeforeEach
    fun setUp() {
        environmentHandler = KubernetesEnvironmentHandler(kerutaAgentService)
    }

    @Test
    fun `setupEnvironmentVariables should include the latest release URL`() {
        // Given
        val latestReleaseUrl = "https://github.com/kigawa-net/keruta-agent/releases/download/v1.0.0/keruta-agent-linux-amd64"
        `when`(kerutaAgentService.getLatestReleaseUrl()).thenReturn(latestReleaseUrl)

        // When
        val envVars = environmentHandler.setupEnvironmentVariables(
            repositoryId = "repo-123",
            documentId = "doc-456",
            agentId = "agent-789",
            agentInstallCommand = "install-command",
            agentExecuteCommand = "execute-command"
        )

        // Then
        // Verify that the environment variables include the latest release URL
        val latestReleaseUrlEnvVar = envVars.find { it.name == "KERUTA_AGENT_LATEST_RELEASE_URL" }
        assertNotNull(latestReleaseUrlEnvVar)
        assertEquals(latestReleaseUrl, latestReleaseUrlEnvVar?.value)

        // Verify that the other environment variables are also included
        assertEquals("repo-123", envVars.find { it.name == "KERUTA_REPOSITORY_ID" }?.value)
        assertEquals("doc-456", envVars.find { it.name == "KERUTA_DOCUMENT_ID" }?.value)
        assertEquals("agent-789", envVars.find { it.name == "KERUTA_AGENT_ID" }?.value)
        assertEquals("install-command", envVars.find { it.name == "KERUTA_AGENT_INSTALL_COMMAND" }?.value)
        assertEquals("execute-command", envVars.find { it.name == "KERUTA_AGENT_EXECUTE_COMMAND" }?.value)
        assertEquals("http://keruta-api.keruta.svc.cluster.local", envVars.find { it.name == "KERUTA_API_ENDPOINT" }?.value)

        // Verify that the kerutaAgentService was called
        verify(kerutaAgentService).getLatestReleaseUrl()
    }

    @Test
    fun `setupEnvironmentVariables should use fallback URL when getLatestReleaseUrl throws an exception`() {
        // Given
        `when`(kerutaAgentService.getLatestReleaseUrl()).thenThrow(RuntimeException("Failed to get latest release URL"))

        // When
        val envVars = environmentHandler.setupEnvironmentVariables()

        // Then
        // Verify that the environment variables include the fallback URL
        val latestReleaseUrlEnvVar = envVars.find { it.name == "KERUTA_AGENT_LATEST_RELEASE_URL" }
        assertNotNull(latestReleaseUrlEnvVar)
        assertEquals("https://github.com/kigawa-net/keruta-agent/releases/latest/download/keruta-agent-linux-amd64", latestReleaseUrlEnvVar?.value)

        // Verify that the kerutaAgentService was called
        verify(kerutaAgentService).getLatestReleaseUrl()
    }
}