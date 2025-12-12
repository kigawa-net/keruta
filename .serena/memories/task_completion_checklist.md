# Task Completion Checklist

Before committing changes, ensure the following:

1. **Code Formatting**: Run `cd keruta-api && ./gradlew ktlintFormatAll` to format all Kotlin code
2. **Code Quality Check**: Run `cd keruta-api && ./gradlew ktlintCheckAll` to verify code style
3. **Tests**: Run `cd keruta-api && ./gradlew test` to execute all tests
4. **Build**: Run `cd keruta-api && ./gradlew build` to ensure successful compilation
5. **Integration Tests**: If applicable, run TestContainers-based integration tests

For Go components:
1. **Tests**: Run `cd keruta-agent && go test ./...`
2. **Build**: Run `cd keruta-agent && ./scripts/build.sh`

For KTCP components:
1. **Build**: Run `./gradlew build` in the respective module directory