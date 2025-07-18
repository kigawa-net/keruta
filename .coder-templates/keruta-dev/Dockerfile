
FROM codercom/code-server:latest

USER root

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    git \
    wget \
    unzip \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release \
    build-essential \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

# Install OpenJDK 21
RUN apt-get update && apt-get install -y openjdk-21-jdk && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Docker
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
RUN apt-get update && apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin && rm -rf /var/lib/apt/lists/*

# Install Go (for keruta-agent development)
RUN wget https://go.dev/dl/go1.21.5.linux-amd64.tar.gz && \
    tar -C /usr/local -xzf go1.21.5.linux-amd64.tar.gz && \
    rm go1.21.5.linux-amd64.tar.gz

ENV PATH=$PATH:/usr/local/go/bin

# Install Node.js and npm (for keruta-admin development)
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Install kubectl for Kubernetes development
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Install MongoDB tools
RUN wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | apt-key add - && \
    echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list && \
    apt-get update && apt-get install -y mongodb-mongosh mongodb-database-tools && \
    rm -rf /var/lib/apt/lists/*

# Install useful development tools
RUN apt-get update && apt-get install -y \
    vim \
    nano \
    htop \
    jq \
    tree \
    && rm -rf /var/lib/apt/lists/*

# Set up workspace directory
RUN mkdir -p /home/coder/workspace
WORKDIR /home/coder/workspace

# Switch back to coder user
USER coder

# Install code-server extensions
RUN code-server --install-extension vscjava.vscode-java-pack && \
    code-server --install-extension ms-vscode.vscode-typescript-next && \
    code-server --install-extension golang.go && \
    code-server --install-extension ms-kubernetes-tools.vscode-kubernetes-tools && \
    code-server --install-extension mongodb.mongodb-vscode && \
    code-server --install-extension redhat.vscode-yaml && \
    code-server --install-extension ms-vscode.vscode-json

# Create startup script
COPY startup.sh /home/coder/startup.sh
RUN chmod +x /home/coder/startup.sh

EXPOSE 8080

CMD ["/home/coder/startup.sh"]
