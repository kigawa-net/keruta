#!/bin/bash

# Startup script for Coder workspace with Claude Code integration
# This script installs Node.js and Claude Code CLI tool

set -euo pipefail

echo "🚀 Starting Coder workspace setup with Claude Code..."

# Log function
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# Error handling
handle_error() {
    log "❌ Error occurred during setup: $1"
    exit 1
}

trap 'handle_error "Script failed at line $LINENO"' ERR

# Update system packages
log "📦 Updating system packages..."
sudo apt-get update -y || handle_error "Failed to update packages"

# Install required system dependencies
log "🔧 Installing system dependencies..."
sudo apt-get install -y curl wget git build-essential python3 python3-pip || handle_error "Failed to install system dependencies"

# Install Node.js using NodeSource repository
log "📥 Installing Node.js ${node_version}..."
curl -fsSL https://deb.nodesource.com/setup_${node_version}.x | sudo -E bash - || handle_error "Failed to add NodeSource repository"
sudo apt-get install -y nodejs || handle_error "Failed to install Node.js"

# Verify Node.js installation
node_installed_version=$(node --version)
npm_installed_version=$(npm --version)
log "✅ Node.js installed: $node_installed_version"
log "✅ npm installed: $npm_installed_version"

# Install Claude Code CLI globally
log "🤖 Installing Claude Code CLI..."
sudo npm install -g @anthropic-ai/claude-code || handle_error "Failed to install Claude Code"

# Verify Claude Code installation
if command -v claude-code &> /dev/null; then
    claude_version=$(claude-code --version 2>/dev/null || echo "version detection failed")
    log "✅ Claude Code installed: $claude_version"
else
    handle_error "Claude Code installation verification failed"
fi

# Set up Claude Code configuration directory
log "⚙️ Setting up Claude Code configuration..."
mkdir -p /home/coder/.config/claude-code
chown -R coder:coder /home/coder/.config

# Create Claude Code configuration file if API key is provided
if [ -n "${claude_api_key}" ] && [ "${claude_api_key}" != "" ]; then
    log "🔑 Configuring Claude Code with provided API key..."
    cat > /home/coder/.config/claude-code/config.json << EOF
{
  "apiKey": "${claude_api_key}",
  "model": "claude-3-5-sonnet-20241022",
  "maxTokens": 4096,
  "temperature": 0.1
}
EOF
    chown coder:coder /home/coder/.config/claude-code/config.json
    chmod 600 /home/coder/.config/claude-code/config.json
    log "✅ Claude Code configured with API key"
else
    log "ℹ️ No API key provided. User will need to configure Claude Code manually with: claude-code auth"
fi

# Install additional development tools
log "🛠️ Installing additional development tools..."
sudo apt-get install -y \
    vim \
    nano \
    htop \
    tree \
    jq \
    unzip \
    zip \
    ca-certificates \
    gnupg \
    lsb-release || log "⚠️ Some additional tools failed to install"

# Install Docker (optional, for containerized development)
if ! command -v docker &> /dev/null; then
    log "🐳 Installing Docker..."
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io || log "⚠️ Docker installation failed"
    sudo usermod -aG docker coder || log "⚠️ Failed to add coder user to docker group"
    log "✅ Docker installed"
fi

# Set up shell aliases for Claude Code
log "🔧 Setting up shell aliases..."
cat >> /home/coder/.bashrc << 'EOF'

# Claude Code aliases and functions
alias cc='claude-code'
alias claude='claude-code'

# Quick Claude Code commands
alias cc-help='claude-code --help'
alias cc-version='claude-code --version'

# Function to quickly start Claude Code with a specific prompt
cc-ask() {
    if [ $# -eq 0 ]; then
        echo "Usage: cc-ask 'your question here'"
        return 1
    fi
    claude-code "$*"
}

# Function to analyze current directory with Claude Code
cc-analyze() {
    claude-code "Please analyze the current directory structure and code. What is this project about and what are the main components?"
}

# Function to help with debugging
cc-debug() {
    if [ $# -eq 0 ]; then
        echo "Usage: cc-debug 'describe your issue here'"
        return 1
    fi
    claude-code "I'm having a debugging issue: $*. Please help me troubleshoot this."
}

EOF

chown coder:coder /home/coder/.bashrc

# Create a welcome message
log "📝 Creating welcome message..."
if [ "${keruta_agent_enabled}" = "true" ]; then
cat > /home/coder/WELCOME_CLAUDE_CODE.md << EOF
# 🤖 Welcome to your Coder Workspace with Claude Code and Keruta Agent!

Your workspace is now equipped with Claude Code CLI tool and Keruta Agent daemon.

## Quick Start

### Claude Code Commands
- \`claude-code\` or \`cc\` - Start Claude Code
- \`claude-code --help\` - Show help
- \`claude-code --version\` - Show version

### Custom Claude Code Aliases
- \`cc-ask "your question"\` - Ask Claude a quick question
- \`cc-analyze\` - Analyze current directory structure
- \`cc-debug "issue description"\` - Get debugging help

### Keruta Agent Commands
- \`ka-status\` - Check daemon status
- \`ka-logs\` - View daemon logs (real-time)
- \`ka-restart\` - Restart daemon
- \`ka-stop\` - Stop daemon
- \`ka-start\` - Start daemon
- \`ka-check\` - Full status check
- \`ka-config\` - Show configuration

### First Time Setup
If you haven't configured your Claude API key yet:
\`\`\`bash
claude-code auth
\`\`\`

### Configuration Locations
- Claude Code config: \`~/.config/claude-code/config.json\`
- Keruta Agent config: \`~/.config/keruta-agent/config.yaml\`
- Keruta Agent logs: \`~/.keruta-agent.log\`

## Features Available
- ✅ Node.js ${node_version}
- ✅ Claude Code CLI
- ✅ Keruta Agent Daemon
- ✅ Git
- ✅ Docker
- ✅ Common development tools

## Keruta Agent Status
The Keruta Agent is running as a systemd daemon and will automatically:
- Poll for new tasks from the Keruta API
- Execute received tasks
- Report task status and logs
- Handle graceful shutdown

Check the status with: \`ka-status\`

## Need Help?
- Claude Code: Run \`claude-code --help\` or visit https://www.anthropic.com/claude-code
- Keruta Agent: Run \`keruta-agent --help\` or check logs with \`ka-logs\`
- Ask Claude directly: \`cc-ask "How do I use this workspace?"\`

Happy coding! 🚀
EOF
else
cat > /home/coder/WELCOME_CLAUDE_CODE.md << 'EOF'
# 🤖 Welcome to your Coder Workspace with Claude Code!

Your workspace is now equipped with Claude Code CLI tool.

## Quick Start

### Basic Commands
- `claude-code` or `cc` - Start Claude Code
- `claude-code --help` - Show help
- `claude-code --version` - Show version

### Custom Aliases
- `cc-ask "your question"` - Ask Claude a quick question
- `cc-analyze` - Analyze current directory structure
- `cc-debug "issue description"` - Get debugging help

### First Time Setup
If you haven't configured your API key yet:
```bash
claude-code auth
```

### Configuration Location
- Config file: `~/.config/claude-code/config.json`
- Logs: Check with `claude-code --help` for log locations

## Features Available
- ✅ Node.js ${node_version}
- ✅ Claude Code CLI
- ✅ Git
- ✅ Docker
- ✅ Common development tools

## Need Help?
- Run `claude-code --help` for CLI documentation
- Visit: https://www.anthropic.com/claude-code
- Ask Claude directly: `cc-ask "How do I use Claude Code?"`

Happy coding! 🚀
EOF
fi

chown coder:coder /home/coder/WELCOME_CLAUDE_CODE.md

# Install keruta-agent
log "🔧 Installing keruta-agent..."
if [ "${keruta_agent_enabled}" = "true" ] && [ -n "${keruta_agent_url}" ] && [ "${keruta_agent_url}" != "" ]; then
    # Download keruta-agent binary
    KERUTA_AGENT_VERSION="${keruta_agent_version:-latest}"
    KERUTA_AGENT_URL="${keruta_agent_url}/keruta-agent-${KERUTA_AGENT_VERSION}"
    
    log "📥 Downloading keruta-agent from: $KERUTA_AGENT_URL"
    curl -fsSL "$KERUTA_AGENT_URL" -o /tmp/keruta-agent || handle_error "Failed to download keruta-agent"
    
    # Install the binary
    sudo chmod +x /tmp/keruta-agent
    sudo mv /tmp/keruta-agent /usr/local/bin/keruta-agent
    
    # Verify installation
    if command -v keruta-agent &> /dev/null; then
        log "✅ keruta-agent installed successfully"
        keruta_agent_version_output=$(keruta-agent --version 2>/dev/null || echo "version detection failed")
        log "📋 keruta-agent version: $keruta_agent_version_output"
    else
        log "⚠️ keruta-agent installation verification failed"
    fi
    
    # Create keruta-agent configuration
    log "⚙️ Setting up keruta-agent configuration..."
    mkdir -p /home/coder/.config/keruta-agent
    cat > /home/coder/.config/keruta-agent/config.yaml << EOF
# keruta-agent configuration
api:
  url: "${keruta_api_url:-http://keruta-api:8080}"
  token: "${keruta_api_token:-}"
  timeout: 30s

agent:
  workspace_id: "${CODER_WORKSPACE_ID:-}"
  poll_interval: 10s
  log_level: info
  
daemon:
  pid_file: "/home/coder/.keruta-agent.pid"
  log_file: "/home/coder/.keruta-agent.log"
EOF
    
    chown -R coder:coder /home/coder/.config/keruta-agent
    
    # Create systemd service for keruta-agent daemon
    log "🔧 Creating keruta-agent systemd service..."
    sudo tee /etc/systemd/system/keruta-agent.service > /dev/null << EOF
[Unit]
Description=Keruta Agent Daemon
After=network.target
Wants=network.target

[Service]
Type=simple
User=coder
Group=coder
WorkingDirectory=/home/coder
Environment=HOME=/home/coder
Environment=CODER_WORKSPACE_ID=${CODER_WORKSPACE_ID:-}
Environment=KERUTA_API_URL=${keruta_api_url:-http://keruta-api:8080}
Environment=KERUTA_API_TOKEN=${keruta_api_token:-}
ExecStart=/usr/local/bin/keruta-agent daemon --workspace-id=\${CODER_WORKSPACE_ID} --log-file=/home/coder/.keruta-agent.log --pid-file=/home/coder/.keruta-agent.pid
ExecReload=/bin/kill -HUP \$MAINPID
KillMode=process
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF
    
    # Enable and start the service
    sudo systemctl daemon-reload
    sudo systemctl enable keruta-agent.service
    sudo systemctl start keruta-agent.service
    
    log "✅ keruta-agent daemon configured and started"
    
    # Add keruta-agent aliases to bashrc
    cat >> /home/coder/.bashrc << 'EOF'

# keruta-agent aliases and functions
alias ka='keruta-agent'
alias keruta='keruta-agent'

# Quick keruta-agent commands
alias ka-status='sudo systemctl status keruta-agent'
alias ka-logs='sudo journalctl -u keruta-agent -f'
alias ka-restart='sudo systemctl restart keruta-agent'
alias ka-stop='sudo systemctl stop keruta-agent'
alias ka-start='sudo systemctl start keruta-agent'

# Function to check keruta-agent daemon status
ka-check() {
    echo "=== Keruta Agent Status ==="
    sudo systemctl is-active keruta-agent
    echo "=== Last 10 Log Lines ==="
    sudo journalctl -u keruta-agent -n 10 --no-pager
}

# Function to show keruta-agent configuration
ka-config() {
    echo "=== Keruta Agent Configuration ==="
    cat /home/coder/.config/keruta-agent/config.yaml 2>/dev/null || echo "Configuration file not found"
}

EOF
    
else
    log "ℹ️ No keruta-agent URL provided. Skipping keruta-agent installation."
fi

# Final setup
log "🏁 Finalizing setup..."
# Ensure proper ownership of home directory
sudo chown -R coder:coder /home/coder
# Create a marker file to indicate setup completion
touch /home/coder/.claude-code-setup-complete

log "🎉 Coder workspace with Claude Code setup completed successfully!"
log "📍 Location: /home/coder"
log "📖 Read WELCOME_CLAUDE_CODE.md for usage instructions"

# Display summary
echo ""
echo "════════════════════════════════════════════════════════════════"
echo "🎯 SETUP SUMMARY"
echo "════════════════════════════════════════════════════════════════"
echo "✅ Node.js: $node_installed_version"
echo "✅ npm: $npm_installed_version" 
echo "✅ Claude Code: Installed and configured"
echo "✅ Development tools: Ready"
echo "✅ Docker: Available"
if [ "${keruta_agent_enabled}" = "true" ]; then
    echo "✅ Keruta Agent: Daemon running"
else
    echo "ℹ️  Keruta Agent: Disabled"
fi
echo ""
if [ -n "${claude_api_key}" ] && [ "${claude_api_key}" != "" ]; then
    echo "🔑 Claude API Key: Configured automatically"
else
    echo "⚠️  Claude API Key: Run 'claude-code auth' to configure"
fi
if [ "${keruta_agent_enabled}" = "true" ]; then
    echo ""
    echo "🤖 Keruta Agent Commands:"
    echo "   ka-status  - Check daemon status"
    echo "   ka-logs    - View daemon logs"
    echo "   ka-restart - Restart daemon"
    echo "   ka-check   - Full status check"
fi
echo ""
echo "🚀 Ready to start coding with Claude Code!"
echo "   Type 'cc-ask \"Hello Claude!\"' to test your setup"
if [ "${keruta_agent_enabled}" = "true" ]; then
    echo "   Type 'ka-status' to check Keruta Agent"
fi
echo "════════════════════════════════════════════════════════════════"