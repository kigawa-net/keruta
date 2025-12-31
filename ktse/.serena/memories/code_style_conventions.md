# Code Style and Conventions for ktse

## Kotlin Style
- Uses standard Kotlin naming conventions
- Object-oriented design with `object` classes for singleton components
- Functional programming elements (when expressions, lambda functions)
- Package structure: `net.kigawa.keruta.ktse`

## Code Patterns
- **Module Pattern**: Each feature is encapsulated in a module object (e.g., `WebsocketModule`, `JwtModule`)
- **Functional Error Handling**: Uses `Res<T, E>` pattern for operation results
- **Extension Functions**: Leverages Kotlin's extension function capabilities
- **Coroutines**: Uses `suspend` functions for asynchronous operations

## Naming Conventions
- Classes: PascalCase (e.g., `KerutaTaskServer`, `WebsocketConnection`)
- Functions: camelCase (e.g., `websocketModule()`, `receive()`)
- Variables: camelCase (e.g., `jsonSerializer`, `ktcpServer`)
- Constants: UPPER_SNAKE_CASE (not extensively used)
- Packages: lowercase with domain hierarchy

## Documentation
- Limited inline documentation in current codebase
- Relies on self-documenting code with meaningful names
- Logger usage for debugging and error tracking

## Error Handling
- Uses `Res.Err` and `Res.Ok` pattern for operation results
- Logger.error() for error reporting with context
- Graceful error handling in WebSocket message processing

## Testing Approach
- No existing tests (to be added)
- Should follow Kotlin testing best practices
- Integration testing for WebSocket functionality
- Unit testing for individual modules