# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

* ユーザーには日本語で応答する
* 大きなファイルは細分化する

## Documents

* 最初にドキュメントを読む
* 常に`/README.md`を更新
* 常に`/todo.md`を更新
* 常に`/structure.md`を更新
* 常にドキュメントを更新する

## Programs

* 純粋関数を使う
* SOLID原則に従う

## Development Commands

### Building and Running
```bash
# Build the entire project
./gradlew build

# Run the Spring Boot application
./gradlew :api:bootRun

# Build and run with Docker
docker-compose up -d

# Build just the Go agent
cd keruta-agent && ./scripts/build.sh
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with detailed output
./gradlew test --continue

# Run Go agent tests
cd keruta-agent && go test ./...
```

### Code Quality
```bash
# Check code style (all modules)
./gradlew ktlintCheckAll

# Format code (all modules)
./gradlew ktlintFormatAll

# Clean build
./gradlew clean
```

### Database and Services
```bash
# Start MongoDB only
docker-compose up -d mongodb

# Stop all services
docker-compose down

# View application logs
docker-compose logs -f
```

## Architecture Overview

Keruta is a Kubernetes-native task execution system with two main components:

### Core Components
1. **Spring Boot API Server** (Kotlin) - Main orchestration service
2. **Keruta Agent** (Go) - Task execution runtime in Kubernetes pods
3. **MongoDB** - Primary data store
4. **Kubernetes** - Container orchestration and job execution

### Multi-Module Structure
- `core:domain` - Domain models (Task, Agent, Repository, etc.)
- `core:usecase` - Business logic and use cases
- `infra:persistence` - MongoDB repository implementations
- `infra:security` - Security configuration (currently permissive)
- `infra:app` - Kubernetes integration and job orchestration
- `api` - REST controllers and web layer

### Key Domain Models
- **Task**: Executable units with status, priority, and Git repository association
- **Agent**: Execution runtimes with language support and current task assignment
- **Repository**: Git repositories with setup scripts and storage configuration
- **Document**: Context documents that can be attached to tasks

## Task Execution Flow

1. Tasks are created via API or admin interface at `/admin`
2. `BackgroundTaskProcessor` polls for pending tasks
3. `KubernetesJobCreator` creates Kubernetes Jobs with:
   - Init containers for Git repository cloning
   - Main containers running the Keruta Agent
   - Shared workspace volumes
4. Agents execute tasks and communicate status via HTTP API calls
5. Real-time logs are streamed through WebSocket connections

## Important Implementation Details

### Kubernetes Integration
- Uses Kubernetes Jobs for task execution
- Supports PersistentVolumeClaims for Git repository storage
- Configurable resource limits per task
- Dynamic namespace and storage class support

### Agent Communication
The Go agent communicates with the Spring Boot API using HTTP:
- `PUT /api/v1/tasks/{id}/status` - Status updates
- `GET /api/v1/tasks/{id}/script` - Script retrieval
- `POST /api/v1/tasks/{id}/logs/stream` - Log streaming

### Security Model
Currently implements permissive security suitable for internal environments:
- No authentication required on API endpoints
- CORS enabled for cross-origin requests
- CSRF disabled for API endpoints

### Database Configuration
MongoDB connection is configured via environment variables:
- `SPRING_DATA_MONGODB_HOST` (default: localhost)
- `SPRING_DATA_MONGODB_PORT` (default: 27017)
- `SPRING_DATA_MONGODB_DATABASE` (default: keruta)
- `SPRING_DATA_MONGODB_USERNAME` (default: admin)
- `SPRING_DATA_MONGODB_PASSWORD` (default: password)

## Key API Endpoints

### Task Management
- `GET /api/v1/tasks` - List all tasks
- `POST /api/v1/tasks` - Create new task
- `PUT /api/v1/tasks/{id}/status` - Update task status
- `GET /api/v1/tasks/{id}/logs` - Get task logs
- `GET /api/v1/tasks/{id}/script` - Get task script

### Agent Management
- `GET /api/v1/agents` - List all agents
- `POST /api/v1/agents` - Create new agent
- `PATCH /api/v1/agents/{id}/status` - Update agent status

### Repository Management
- `GET /api/v1/repositories` - List repositories
- `POST /api/v1/repositories` - Create repository
- `GET /api/v1/repositories/{id}/validate` - Validate repository

## Development Environment

### Local Setup
1. Start MongoDB: `docker-compose up -d mongodb`
2. Run application: `./gradlew :api:bootRun`
3. Access admin interface: http://localhost:8080/admin
4. Access API docs: http://localhost:8080/swagger-ui.html

### Docker Development
- Full stack: `docker-compose up -d`
- Services: MongoDB (27017), Keycloak (8180), PostgreSQL (5432)
- Application: http://localhost:8080

## Code Style and Quality

### Kotlin Style
- Uses ktlint for code formatting and style checking
- Configuration in `.editorconfig`
- Some rules temporarily disabled for migration ease
- New code should follow full ktlint standards

### Go Style
- Standard Go formatting with `gofmt`
- Uses testify for testing
- Cobra for CLI structure

## Testing Strategy

### Kotlin Tests
- JUnit 5 framework
- TestContainers for integration testing with MongoDB
- Mockito for mocking dependencies
- Tests located in `src/test/kotlin` in each module

### Go Tests
- Built-in Go testing framework
- Testify for assertions
- Tests in `*_test.go` files

## Deployment

### Kubernetes Deployment
- Deployment manifests in `kigawa-net-k8s/keruta/`
- Uses service account `keruta-sa` with appropriate RBAC
- Configurable via environment variables and secrets
- Health checks on `/api/health` endpoint

### Docker Images
- Main application: Built from root `Dockerfile`
- Agent: Built from `keruta-agent/Dockerfile`
- Uses Harbor registry: `harbor.kigawa.net/library/keruta`
