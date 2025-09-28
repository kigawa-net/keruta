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
cd keruta-api && docker-compose up -d mongodb && ./gradlew bootRun

# Full development environment with all services
cd keruta-api && docker-compose up -d

# Stop all services
cd keruta-api && docker-compose down
```

### Building and Running
```bash
# Build the API project
cd keruta-api && ./gradlew build

# Run the Spring Boot API server
cd keruta-api && ./gradlew bootRun

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

# Run specific module tests (Note: This is a single-module project)
cd keruta-api && ./gradlew test

# Run tests for keruta-executor
cd keruta-executor && ./gradlew test

# Run Go agent tests
cd keruta-agent && go test ./...

# Run integration tests with TestContainers (requires Docker)
cd keruta-api && ./gradlew test
```

### Code Quality
```bash
# Format and check all code (run before committing)
cd keruta-api && ./gradlew ktlintFormatAll && ./gradlew ktlintCheckAll

# Check code style (all modules in keruta-api)
cd keruta-api && ./gradlew ktlintCheckAll

# Format code (all modules in keruta-api)
cd keruta-api && ./gradlew ktlintFormatAll

# Check/Format main project
cd keruta-api && ./gradlew ktlintCheck
cd keruta-api && ./gradlew ktlintFormat

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
- **Package Structure** (single Gradle project with logical package separation):
  - `net.kigawa.keruta.core.domain` - Domain models (Session, Workspace, Document, etc.)
  - `net.kigawa.keruta.core.usecase` - Business logic and use cases for session/workspace management
  - `net.kigawa.keruta.infra.persistence` - MongoDB repository implementations
  - `net.kigawa.keruta.infra.security` - Security configuration (currently permissive)
  - `net.kigawa.keruta.infra.app` - Coder integration, workspace orchestration, and coroutine management
  - `net.kigawa.keruta.api` - REST controllers and web layer
  - `generated-api` - OpenAPI generated code subproject

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
- **Automatic Token Management**: Coder session tokens automatically refresh every 24 hours
- **Kubernetes Secret Integration**: Tokens managed via K8s secrets for production deployment

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
- **Coder Token Auto-Refresh**: CoderTemplateService now automatically refreshes session tokens every 24 hours
- **K8s Secret Integration**: Coder tokens managed via Kubernetes secrets in production deployments
- **Database-Free Workspace Management**: Workspaces no longer stored in database, managed directly via Coder API
- **Multi-Endpoint Coder Support**: Automatic fallback across multiple Coder API endpoint formats for compatibility

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

### Coder Configuration (Executor)
Coder integration is configured via environment variables:
- `KERUTA_EXECUTOR_CODER_BASE_URL` - Coder server URL
- `KERUTA_EXECUTOR_CODER_TOKEN` - Coder session token (managed via K8s secrets in production)
- `KERUTA_EXECUTOR_CODER_ENABLE_CLI_FALLBACK` - Enable CLI fallback for token refresh (default: false)
- `KERUTA_EXECUTOR_API_BASE_URL` - keruta-api base URL (default: http://localhost:8080)

**Token Management**: The CoderTemplateService automatically refreshes session tokens every 24 hours using existing tokens. It supports two refresh methods:

1. **API-based refresh** (default): Uses `/api/v2/users/me/tokens` endpoint to create new tokens
2. **CLI fallback** (optional): Falls back to `coder login` command if API refresh fails

**Token Refresh Methods**:
- **Short-term sessions**: Automatically refreshed every 24 hours (default duration)
- **Long-term API tokens**: Can be regenerated using `coder tokens regen <TOKEN_ID>`
- **Manual refresh**: `coder logout && coder login <URL>` for immediate re-authentication

In Kubernetes deployments, tokens are stored in secrets and mounted as environment variables.

## Key API Endpoints

### Session Management
- `GET /api/v1/sessions` - List all sessions
- `POST /api/v1/sessions` - Create new session
- `PUT /api/v1/sessions/{id}/status` - Update session status (restricted - returns 403 for user requests)
- `GET /api/v1/sessions/{id}` - Get session details

### Workspace Management (via Executor)
- `GET /api/v1/workspaces` - List all workspaces (proxied to executor)
- `POST /api/v1/workspaces` - Create new workspace (proxied to executor)
- `POST /api/v1/workspaces/{id}/start` - Start workspace (proxied to executor)
- `POST /api/v1/workspaces/{id}/stop` - Stop workspace (proxied to executor)
- `GET /api/v1/workspaces/templates` - List available templates (proxied to executor)
- `POST /api/v1/sessions/{id}/sync-status` - Sync session status with workspace state
- `GET /api/v1/sessions/{id}/workspaces` - Get workspaces for specific session

### Document Management
- `GET /api/v1/documents` - List all documents
- `POST /api/v1/documents` - Create new document
- `GET /api/v1/documents/{id}` - Get document details

## Development Environment

### Local Setup
1. Start MongoDB: `cd keruta-api && docker-compose up -d mongodb`
2. Run API server: `cd keruta-api && ./gradlew bootRun`  
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

- **keruta-api**: Single-module Gradle project with clean architecture package structure and direct MongoDB access
  - Main project with logical package separation
  - `generated-api` - OpenAPI generated code subproject
  - Package structure follows layered architecture pattern
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
- **Coder authentication errors**: Check KERUTA_EXECUTOR_CODER_TOKEN environment variable or K8s secret configuration
- **Coder API endpoint errors**: Check logs for 404/405 errors - system automatically tries multiple endpoint formats
- **JSON parsing errors**: Ensure nullable fields in DTOs for Coder API responses (version, createdAt, updatedAt)
- **CORS errors**: When allowCredentials is true, allowedOrigins cannot contain "*" - set allowCredentials(false) in CorsConfig.kt
- **Java version issues**: Use JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 for Java 21

### Development Workflow
1. Start MongoDB: `cd keruta-api && docker-compose up -d mongodb`
2. Format code: `cd keruta-api && ./gradlew ktlintFormatAll`
3. Run tests: `cd keruta-api && ./gradlew test`
4. Start API: `cd keruta-api && ./gradlew bootRun`
5. Access admin: http://localhost:8080/admin

### Coder API Integration Details
Coder workspace creation uses multiple endpoint formats for compatibility:
1. **Primary**: `/api/v2/users/me/workspaces` (official Coder API v2)
2. **Fallback 1**: `/api/v2/organizations/default/members/me/workspaces` (organization-based)
3. **Fallback 2**: `/api/v2/workspaces` (legacy format)

Template selection algorithm:
1. Match session tags with template names/descriptions
2. Prefer templates containing "keruta" in the name
3. Use first available template as fallback

## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.
