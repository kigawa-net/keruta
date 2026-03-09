# Development Commands for ktse

## Building
```bash
./gradlew build          # Build the project
./gradlew assemble       # Assemble without running tests
./gradlew clean build    # Clean and rebuild
```

## Testing
```bash
./gradlew test           # Run all tests
./gradlew test --info    # Run tests with detailed output
```

## Code Quality (when ktlint is configured)
```bash
./gradlew ktlintCheck    # Check code style
./gradlew ktlintFormat   # Format code
```

## Running
```bash
./gradlew run            # Run the application
```

## Utility Commands
```bash
./gradlew dependencies   # Show dependency tree
./gradlew projects       # Show project structure
./gradlew tasks          # Show available tasks
```

## Debugging
```bash
./gradlew --info build   # Build with detailed logging
./gradlew --debug test   # Run tests in debug mode
```