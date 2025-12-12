# Suggested Commands for Development

## Quick Start
```bash
# Start MongoDB and run API server (most common development setup)
cd keruta-api && docker-compose up -d mongodb && ./gradlew bootRun

# Full development environment with all services
cd keruta-api && docker-compose up -d

# Stop all services
cd keruta-api && docker-compose down
```

## Building and Running
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

## Testing
```bash
# Run all tests (most common)
cd keruta-api && ./gradlew test

# Run tests with detailed output and continue on failure
cd keruta-api && ./gradlew test --continue

# Run tests for keruta-executor
cd keruta-executor && ./gradlew test

# Run Go agent tests
cd keruta-agent && go test ./...
```

## Code Quality
```bash
# Format and check all code (run before committing)
cd keruta-api && ./gradlew ktlintFormatAll && ./gradlew ktlintCheckAll

# Check code style (all modules in keruta-api)
cd keruta-api && ./gradlew ktlintCheckAll

# Format code (all modules in keruta-api)
cd keruta-api && ./gradlew ktlintFormatAll

# Check keruta-executor
cd keruta-executor && ./gradlew ktlintCheck && ./gradlew ktlintFormat
```

## Database and Services
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

## Development Workflow
1. Start MongoDB: `cd keruta-api && docker-compose up -d mongodb`
2. Format code: `cd keruta-api && ./gradlew ktlintFormatAll`
3. Run tests: `cd keruta-api && ./gradlew test`
4. Start API: `cd keruta-api && ./gradlew bootRun`
5. Access admin: http://localhost:8080/admin