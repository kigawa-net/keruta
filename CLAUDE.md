# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

* ユーザーには日本語で応答する
* 大きなファイルは細分化する

## Programs

* 純粋関数を使う
* SOLID原則に従う
* All service classes must be marked `open` for Spring CGLIB proxy creation
* Use `@Component`, `@Service`, `@Repository` annotations consistently

## Development Commands

### Quick Start
```bash
# Start MongoDB and run API server (most common development setup)
cd keruta-api && docker-compose up -d mongodb && ./gradlew :api:bootRun

# Full development environment with all services
docker-compose up -d

# Stop all services
docker-compose down
```

### Building and Running
```bash
# Build the API project
cd keruta-api && ./gradlew build

# Run the Spring Boot API server
cd keruta-api && ./gradlew :api:bootRun

# Run the Keruta Executor (for Coder workspace management)
cd keruta-executor && ./gradlew bootRun

# Build just the Go agent (for task execution)
cd keruta-agent && ./scripts/build.sh

# Clean and rebuild everything
cd keruta-api && ./gradlew clean build
```

### Testing
```bash
# Run all tests (most common)
cd keruta-api && ./gradlew test

# Run tests with detailed output and continue on failure
cd keruta-api && ./gradlew test --continue

# Run specific module tests
cd keruta-api && ./gradlew :core:domain:test
cd keruta-api && ./gradlew :core:usecase:test
cd keruta-api && ./gradlew :infra:persistence:test
cd keruta-api && ./gradlew :infra:security:test
cd keruta-api && ./gradlew :infra:app:test
cd keruta-api && ./gradlew :api:test

# Run tests for keruta-executor
cd keruta-executor && ./gradlew test

# Run Go agent tests
cd keruta-agent && go test ./...

# Run integration tests with TestContainers (requires Docker)
cd keruta-api && ./gradlew :infra:persistence:test
```

### Code Quality
```bash
# Format and check all code (run before committing)
cd keruta-api && ./gradlew ktlintFormatAll && ./gradlew ktlintCheckAll

# Check code style (all modules in keruta-api)
cd keruta-api && ./gradlew ktlintCheckAll

# Format code (all modules in keruta-api)
cd keruta-api && ./gradlew ktlintFormatAll

# Check/Format specific module
cd keruta-api && ./gradlew :core:domain:ktlintCheck
cd keruta-api && ./gradlew :api:ktlintFormat

# Check keruta-executor
cd keruta-executor && ./gradlew ktlintCheck && ./gradlew ktlintFormat
```

### Database and Services
```bash
# Start MongoDB only (most common for development)
cd keruta-api && docker-compose up -d mongodb

# Start MongoDB with logs
cd keruta-api && docker-compose up mongodb

# View application logs
docker-compose logs -f keruta-api
docker-compose logs -f keruta-executor

# Reset database (stops and removes containers with data)
docker-compose down -v && docker-compose up -d mongodb
```

## Architecture Overview

Keruta is a Coder workspace management system with three main components:

### Core Components
1. **Spring Boot API Server** (Kotlin) - Main API service for session and workspace management
2. **Keruta Executor** (Kotlin/Spring Boot) - Coder workspace monitoring and management
3. **Keruta Agent** (Go) - Task execution runtime for external processes
4. **MongoDB** - Primary data store
5. **/todo.md** - todolist

### Multi-Module Structure

#### API Server (keruta-api)
- `core:domain` - Domain models (Session, Workspace, Document, etc.)
- `core:usecase` - Business logic and use cases for session/workspace management
- `infra:persistence` - MongoDB repository implementations
- `infra:security` - Security configuration (currently permissive)
- `infra:app` - Coder integration, workspace orchestration, and coroutine management
- `api` - REST controllers and web layer

#### Executor (keruta-executor)
- **Session Monitoring** - Standalone Spring Boot application that monitors session states
- **API-only Data Access** - No direct database access, communicates only via REST API
- **Background Scheduling** - Uses `@Scheduled` methods for session and workspace monitoring
- **Workspace Management** - Manages Coder workspace lifecycle via API calls

### Key Domain Models
- **Session**: User sessions with associated workspaces and status management (metadata removed, status updates restricted)
- **Workspace**: Coder workspaces with templates, URLs, and lifecycle state (generic container resource fields)
- **Document**: Context documents that can be attached to sessions
- **WorkspaceTemplate**: Coder templates for workspace creation

## Session and Workspace Management Flow

### Session Creation and Workspace Lifecycle
1. Sessions are created via API or admin interface at `/admin`
2. Workspaces are automatically created when sessions are created (1:1 relationship)
3. Session status transitions: PENDING → ACTIVE → (COMPLETED/TERMINATED)
4. Workspace status transitions: PENDING → STARTING → RUNNING → STOPPED

### Executor-based Workspace Monitoring
1. Keruta Executor runs as a separate Spring Boot application
2. `SessionMonitoringService` polls keruta-api for session states every 30-60 seconds
3. Monitors PENDING sessions and ensures workspaces are created
4. Monitors ACTIVE sessions and ensures workspaces are running
5. Manages workspace start/stop lifecycle via Coder API calls

## Important Implementation Details

### Coder Integration
- Integrates with Coder API for workspace management
- Supports custom Terraform templates for workspace creation
- Manages workspace lifecycle (create, start, stop, delete)
- Template selection based on session requirements
- Japanese session name normalization for Coder compatibility

### Executor Communication
The Keruta Executor communicates with the Spring Boot API using HTTP:
- `GET /api/v1/sessions?status=PENDING` - Get pending sessions
- `GET /api/v1/sessions?status=ACTIVE` - Get active sessions
- `GET /api/v1/workspaces?sessionId={id}` - Get session workspaces
- `POST /api/v1/workspaces` - Create new workspace
- `PUT /api/v1/sessions/{id}/status` - Update session status (system only)
- `POST /api/v1/workspaces/{id}/start` - Start workspace

### Recent Architecture Changes
- **Module Simplification**: infra:core merged into infra:app for reduced complexity
- **Kubernetes Removal**: All Kubernetes-specific code removed, generic container resource fields used
- **Status Security**: Session status updates restricted to system only (user updates return 403)
- **Metadata Cleanup**: Session metadata field removed from all layers
- **Logger Fix**: WorkspaceTaskExecutionService logger moved to companion object for thread safety

### Session and Workspace Architecture
- **1:1 Session-Workspace Relationship** - Each session has exactly one associated workspace
- **Automatic Workspace Creation** - Workspaces are created automatically when sessions are created
- **Status Synchronization** - Session status updates trigger workspace state changes
- **Template-based Creation** - Uses Coder templates for consistent workspace environments

### Security Model
Currently implements permissive security suitable for internal environments:
- No authentication required on API endpoints
- CORS enabled for cross-origin requests
- CSRF disabled for API endpoints
- Session status updates are restricted - only system can modify status

### Database Configuration
MongoDB connection is configured via environment variables:
- `SPRING_DATA_MONGODB_HOST` (default: localhost)
- `SPRING_DATA_MONGODB_PORT` (default: 27017)
- `SPRING_DATA_MONGODB_DATABASE` (default: keruta)
- `SPRING_DATA_MONGODB_USERNAME` (default: admin)
- `SPRING_DATA_MONGODB_PASSWORD` (default: password)

## Key API Endpoints

### Session Management
- `GET /api/v1/sessions` - List all sessions
- `POST /api/v1/sessions` - Create new session
- `PUT /api/v1/sessions/{id}/status` - Update session status (restricted - returns 403 for user requests)
- `GET /api/v1/sessions/{id}` - Get session details

### Workspace Management
- `GET /api/v1/workspaces` - List all workspaces
- `POST /api/v1/workspaces` - Create new workspace
- `POST /api/v1/workspaces/{id}/start` - Start workspace
- `POST /api/v1/workspaces/{id}/stop` - Stop workspace
- `GET /api/v1/workspaces/templates` - List available templates

### Document Management
- `GET /api/v1/documents` - List all documents
- `POST /api/v1/documents` - Create new document
- `GET /api/v1/documents/{id}` - Get document details

## Development Environment

### Local Setup
1. Start MongoDB: `cd keruta-api && docker-compose up -d mongodb`
2. Run API server: `cd keruta-api && ./gradlew :api:bootRun`  
3. (Optional) Run executor: `cd keruta-executor && ./gradlew bootRun`
4. Access admin interface: http://localhost:8080/admin
5. Access API docs: http://localhost:8080/swagger-ui.html

### Docker Development
- Full stack: `docker-compose up -d`
- Services: MongoDB (27017), Keycloak (8180), PostgreSQL (5432)
- Application: http://localhost:8080

## Code Style and Quality

### Kotlin Style
- Uses ktlint for code formatting and style checking
- Configuration in `.editorconfig`
- Some rules temporarily disabled for migration ease: wildcard imports, filename matching, max line length
- All service classes must be marked `open` for Spring CGLIB proxy creation
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

### Container Deployment
- Docker images built from respective Dockerfiles
- Configurable via environment variables and secrets
- Health checks on `/api/health` endpoint

### Docker Images
- API Server: Built from `keruta-api/Dockerfile`
- Executor: Built from `keruta-executor/Dockerfile`  
- Agent: Built from `keruta-agent/Dockerfile`
- Uses Harbor registry: `harbor.kigawa.net/library/keruta`

## Project Structure Notes

- **keruta-api**: Multi-module Gradle project (5 modules) with simplified clean architecture and direct MongoDB access
  - `core:domain` - Domain models and entities
  - `core:usecase` - Business logic and use cases
  - `infra:persistence` - MongoDB repository implementations
  - `infra:security` - Security configuration
  - `infra:app` - Coder integration and coroutine management (consolidated from infra:core)
  - `api` - REST controllers and web layer
- **keruta-executor**: Standalone Spring Boot application with API-only data access (no direct DB connection)
- **keruta-agent**: Go CLI application for task execution within containers
- **keruta-admin**: React/Remix frontend for administration
- Configuration is environment-based (Spring profiles, environment variables)
- Tests use TestContainers for integration testing with real databases (API server only)
- Recent simplifications: infra:core merged into infra:app, Kubernetes functionality removed, Session metadata removed

## Common Issues and Solutions

### Build Issues
- **Gradle build cache issues**: Run `cd keruta-api && ./gradlew clean build`
- **ktlint failures**: Run `cd keruta-api && ./gradlew ktlintFormatAll` before building
- **TestContainer failures**: Ensure Docker is running and accessible

### Runtime Issues
- **NullPointerException in scheduled tasks**: Check logger initialization in companion objects
- **MongoDB connection issues**: Verify MongoDB is running and environment variables are set
- **Spring CGLIB proxy issues**: Ensure service classes are marked `open`

### Development Workflow
1. Start MongoDB: `cd keruta-api && docker-compose up -d mongodb`
2. Format code: `cd keruta-api && ./gradlew ktlintFormatAll`
3. Run tests: `cd keruta-api && ./gradlew test`
4. Start API: `cd keruta-api && ./gradlew :api:bootRun`
5. Access admin: http://localhost:8080/admin
