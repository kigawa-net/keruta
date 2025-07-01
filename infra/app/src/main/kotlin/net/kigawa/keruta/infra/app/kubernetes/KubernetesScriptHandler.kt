package net.kigawa.keruta.infra.app.kubernetes

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Handler for Kubernetes scripts.
 * Responsible for setting up scripts and commands for containers.
 */
@Component
class KubernetesScriptHandler {
    private val logger = LoggerFactory.getLogger(KubernetesScriptHandler::class.java)

    /**
     * Sets up script and command for the container.
     *
     */
    fun setupScriptAndCommand(
        originalCommand: List<String>? = null,
        originalArgs: List<String>? = null,
    ): Pair<List<String>, List<String>> {
        // Create a wrapper script that runs the setup script and then the original command
        val wrapperScript = mutableListOf(
            "#!/bin/sh",
            "set -e",
            "",
            "# Run setup script",
            createSetupScript().joinToString("\n"),
            "",
            "# Run original command",
        )

        if (originalCommand != null && originalCommand.isNotEmpty()) {
            // If original command exists, run it
            wrapperScript.add("exec ${originalCommand.joinToString(" ")} ${originalArgs?.joinToString(" ") ?: ""}")
        } else {
            // If no original command, use a default command
            wrapperScript.add("exec /bin/sh -c \"${originalArgs?.joinToString(" ") ?: "echo 'No command specified'"}\"")
        }

        // Set command and args for container
        return Pair(listOf("/bin/sh", "-c"), listOf(wrapperScript.joinToString("\n")))
    }

    /**
     * Creates the setup script for the container.
     *
     * @return The setup script as a list of strings
     */
    fun createSetupScript(): List<String> {
        return listOf(
            "# Install curl if not already installed",
            "if ! command -v curl > /dev/null; then",
            "  if command -v apt-get > /dev/null; then",
            "    apt-get update && apt-get install -y --no-install-recommends curl && apt-get clean && rm -rf /var/lib/apt/lists/*",
            "  elif command -v apk > /dev/null; then",
            "    apk add --no-cache curl",
            "  elif command -v yum > /dev/null; then",
            "    yum install -y curl && yum clean all",
            "  else",
            "    echo 'Warning: curl not found and could not be installed'",
            "  fi",
            "fi",
            "",
            "mkdir -p ./.keruta",
            "",
            "# Check if environment variables are set (they might be empty if ConfigMap doesn't exist)",
            "if [ -z \"\$KERUTA_REPOSITORY_ID\" ]; then",
            "  echo \"KERUTA_REPOSITORY_ID is not set or empty. Checking for local script.\"",
            "  if [ -f ./.keruta/install.sh ]; then",
            "    echo \"Running local setup script...\"",
            "    sh ./.keruta/install.sh",
            "  else",
            "    echo \"No local script found. Skipping repository script.\"",
            "  fi",
            "else",
            "  echo \"Fetching install script for repository: \$KERUTA_REPOSITORY_ID\"",
            "  SCRIPT_URL=\"\${KERUTA_API_ENDPOINT}/api/v1/repositories/\${KERUTA_REPOSITORY_ID}/script\"",
            "",
            "  if curl -sfL -o ./.keruta/install.sh \"\${SCRIPT_URL}\" && [ -s ./.keruta/install.sh ]; then",
            "    echo \"Running downloaded setup script...\"",
            "    chmod +x ./.keruta/install.sh",
            "    ./.keruta/install.sh",
            "  else",
            "    echo \"Install script not found or is empty. Skipping execution.\"",
            "  fi",
            "fi",
            "",
            "# Check if document ID is set",
            "if [ -z \"\$KERUTA_DOCUMENT_ID\" ]; then",
            "  echo \"KERUTA_DOCUMENT_ID is not set or empty. Skipping document download.\"",
            "else",
            "  echo \"Fetching document: \$KERUTA_DOCUMENT_ID\"",
            "  DOC_URL=\"\${KERUTA_API_ENDPOINT}/api/v1/documents/\${KERUTA_DOCUMENT_ID}/content\"",
            "  curl -sfL -o ./.keruta/README.md \"\$DOC_URL\"",
            "fi",
            "",
            "# Download and install keruta-agent from the latest release",
            "if [ -z \"\$KERUTA_AGENT_LATEST_RELEASE_URL\" ]; then",
            "  echo \"KERUTA_AGENT_LATEST_RELEASE_URL is not set or empty. Skipping keruta-agent download.\"",
            "else",
            "  echo \"Downloading keruta-agent from: \$KERUTA_AGENT_LATEST_RELEASE_URL\"",
            "  if curl -sfL -o /usr/local/bin/keruta-agent \"\$KERUTA_AGENT_LATEST_RELEASE_URL\"; then",
            "    chmod +x /usr/local/bin/keruta-agent",
            "    echo \"keruta-agent downloaded and installed successfully\"",
            "    # Print version information",
            "    keruta-agent --version || echo \"Failed to get keruta-agent version\"",
            "  else",
            "    echo \"Failed to download keruta-agent from \$KERUTA_AGENT_LATEST_RELEASE_URL\"",
            "  fi",
            "fi",
            "",
            "# Check if agent install command is set",
            "if [ -z \"\$KERUTA_AGENT_INSTALL_COMMAND\" ]; then",
            "  echo \"KERUTA_AGENT_INSTALL_COMMAND is not set or empty. Skipping agent installation.\"",
            "else",
            "  echo \"Installing agent with command: \$KERUTA_AGENT_INSTALL_COMMAND\"",
            "  eval \"\$KERUTA_AGENT_INSTALL_COMMAND\"",
            "fi",
            "",
            "# Check if agent execute command is set",
            "if [ -z \"\$KERUTA_AGENT_EXECUTE_COMMAND\" ]; then",
            "  echo \"KERUTA_AGENT_EXECUTE_COMMAND is not set or empty. Skipping agent execution.\"",
            "else",
            "  echo \"Executing agent with command: \$KERUTA_AGENT_EXECUTE_COMMAND\"",
            "  eval \"\$KERUTA_AGENT_EXECUTE_COMMAND\"",
            "fi",
        )
    }
}
