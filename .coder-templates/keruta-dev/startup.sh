#!/bin/bash

# Keruta Development Environment Startup Script

set -e

echo "=== Keruta Development Environment Setup ==="

# Create workspace directory
mkdir -p /home/coder/workspace
cd /home/coder/workspace

# Set up Go environment
export GOROOT=/usr/local/go
export GOPATH=/home/coder/go
export PATH=$GOROOT/bin:$GOPATH/bin:$PATH

# Create Go workspace
mkdir -p $GOPATH/bin $GOPATH/src $GOPATH/pkg

# Set up Java environment
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Add Docker group (if not already exists)
if ! groups | grep -q docker; then
    echo "Adding user to docker group..."
    sudo usermod -aG docker coder
fi

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -i :$port > /dev/null 2>&1; then
        echo "Port $port is already in use"
        return 1
    fi
    return 0
}

# Function to start MongoDB if not running
start_mongodb() {
    if ! docker ps --format "table {{.Names}}" | grep -q "keruta-mongodb"; then
        echo "Starting MongoDB..."
        if [ -f "keruta-api/docker-compose.yml" ]; then
            cd keruta-api
            docker-compose up -d mongodb
            cd ..
        else
            docker run -d \
                --name keruta-mongodb \
                -p 27017:27017 \
                -e MONGO_INITDB_ROOT_USERNAME=admin \
                -e MONGO_INITDB_ROOT_PASSWORD=password \
                -e MONGO_INITDB_DATABASE=keruta \
                mongo:latest
        fi
        echo "MongoDB started"
    else
        echo "MongoDB is already running"
    fi
}

# Function to build the project
build_project() {
    echo "Building the project..."
    ./gradlew build
    echo "Build completed"
}

# Function to start the API server
start_api_server() {
    if check_port 8080; then
        echo "Starting Keruta API server..."
        ./gradlew :api:bootRun &
        echo "API server started in background"
    else
        echo "Port 8080 is already in use, skipping API server startup"
    fi
}

# Function to setup development environment
setup_dev_environment() {
    echo "Setting up development environment..."
    
    # Create useful aliases
    echo "alias ll='ls -la'" >> ~/.bashrc
    echo "alias la='ls -la'" >> ~/.bashrc
    echo "alias k='kubectl'" >> ~/.bashrc
    echo "alias d='docker'" >> ~/.bashrc
    echo "alias dc='docker-compose'" >> ~/.bashrc
    echo "alias g='git'" >> ~/.bashrc
    
    # Add development paths
    echo "export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64" >> ~/.bashrc
    echo "export GOROOT=/usr/local/go" >> ~/.bashrc
    echo "export GOPATH=/home/coder/go" >> ~/.bashrc
    echo "export PATH=\$GOROOT/bin:\$GOPATH/bin:\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    
    # Git configuration
    git config --global user.name "Coder User"
    git config --global user.email "coder@example.com"
    git config --global init.defaultBranch main
    
    echo "Development environment setup completed"
}

# Main execution
main() {
    echo "Starting Keruta development environment..."
    
    # Setup development environment
    setup_dev_environment
    
    # Check if we're in the keruta directory
    if [ -f "gradlew" ]; then
        echo "Found Keruta project"
        
        # Make gradlew executable
        chmod +x gradlew
        
        # Start MongoDB
        start_mongodb
        
        # Wait for MongoDB to be ready
        echo "Waiting for MongoDB to be ready..."
        sleep 10
        
        # Build the project
        build_project
        
        # Start the API server
        start_api_server
        
    else
        echo "Keruta project not found. Please clone the repository first."
        echo "Run: git clone https://github.com/kigawa-net/keruta.git"
    fi
    
    echo "=== Development Environment Ready ==="
    echo "Available services:"
    echo "- Code Server: http://localhost:8080"
    echo "- Keruta API: http://localhost:8080/api"
    echo "- Keruta Admin: http://localhost:8080/admin"
    echo "- MongoDB: localhost:27017"
    echo ""
    echo "Useful commands:"
    echo "- ./gradlew build               # Build the project"
    echo "- ./gradlew :api:bootRun        # Start API server"
    echo "- ./gradlew test                # Run tests"
    echo "- ./gradlew ktlintFormatAll     # Format code"
    echo "- docker-compose up -d mongodb  # Start MongoDB"
    echo ""
    
    # Start code-server
    exec code-server --bind-addr 0.0.0.0:8080 --auth none --disable-telemetry /home/coder/workspace
}

# Run main function
main "$@"