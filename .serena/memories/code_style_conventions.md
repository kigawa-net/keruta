# Code Style and Conventions

## Kotlin Style
- Uses ktlint for code formatting and style checking
- Configuration in `.editorconfig`
- All service classes must be marked `open` for Spring CGLIB proxy creation
- Use `@Component`, `@Service`, `@Repository` annotations consistently
- Pure functions preferred, SOLID principles followed
- New code should follow full ktlint standards

## Go Style
- Standard Go formatting with `gofmt`
- Uses testify for testing
- Cobra for CLI structure

## Naming Conventions
- Package structure follows layered architecture (domain, usecase, infra)
- Classes use PascalCase
- Functions and variables use camelCase
- Constants use UPPER_SNAKE_CASE

## Architecture Patterns
- Clean Architecture with domain/usecase/infra layers
- Dependency injection with Spring
- Repository pattern for data access
- Service classes for business logic