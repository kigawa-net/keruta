# Todo List

- structure.mdの改善
- keruta-docリポジトリへの完全な移行

## Completed Tasks

- 改善されたエラーレスポンス
    - Change: エラーレスポンスをより丁寧で詳細なものに改善
    - Details: GlobalExceptionHandlerを作成してアプリケーション全体の例外を処理し、RestAuthenticationEntryPointを更新して認証エラーメッセージを改善
    - Location: /infra/security/src/main/kotlin/net/kigawa/keruta/infra/security/config/GlobalExceptionHandler.kt, /infra/security/src/main/kotlin/net/kigawa/keruta/infra/security/config/RestAuthenticationEntryPoint.kt
    - Reason: ユーザーがエラーの原因をより理解しやすくし、適切な対応を取れるようにするため

- Removed authentication from the application
    - Change: Removed OAuth2/Keycloak authentication and made all endpoints publicly accessible
    - Details: Removed OAuth2 login configuration from SecurityConfig, commented out Keycloak and JWT configuration in application.properties, and updated README.md
    - Location: /infra/security/src/main/kotlin/net/kigawa/keruta/infra/security/config/SecurityConfig.kt, /api/src/main/resources/application.properties, /README.md
    - Reason: Simplified application access by removing authentication requirement

- Fixed OAuth2 client configuration issue
    - Change: Updated SecurityConfig to properly configure OAuth2 login
    - Details: Added OAuth2 login configuration to securityFilterChain method and updated imports
    - Location: /infra/security/src/main/kotlin/net/kigawa/keruta/infra/security/config/SecurityConfig.kt
    - Reason: Fixed Spring Security initialization error related to OAuth2 client configuration

- Fixed bootRun task configuration
    - Change: Updated GitHub workflow and README to specify the api module for bootRun task
    - Details: Changed `./gradlew bootRun` to `./gradlew :api:bootRun` and disabled bootRun task in infra/core module
    - Location: /.github/workflows/generate-openapi.yml, /infra/core/build.gradle.kts, /README.md
    - Reason: The main application class is in the api module, not in the infra/core module

- Replaced sleep with health check in GitHub workflow
    - Change: Modified GitHub workflow to use /api/v1/health endpoint instead of fixed sleep
    - Details: Implemented a retry loop with timeout in generate-openapi.yml
    - Location: /.github/workflows/generate-openapi.yml
    - Reason: More reliable application startup detection for OpenAPI generation

- Added health check endpoint for s1leep management
    - Change: Implemented a new health check endpoint at /api/v1/health
    - Details: Created a new HealthController in the v1 API package
    - Location: /api/src/main/kotlin/net/kigawa/keruta/api/v1/HealthController.kt
    - Reason: To provide health status information for s1leep management

- Set up keruta-doc repository migration
    - Change: Prepared keruta-doc directory for migration to a separate repository
    - Details: Created structure.md and todo.md files, updated README.md with migration notice
    - Location: keruta-doc directory and https://github.com/kigawa-net/keruta-doc
    - Reason: To separate documentation from the main codebase for better organization

- Added GitHub Action for automatic OpenAPI specification generation
    - Change: Created a workflow that generates OpenAPI specification files on push
    - Details: Added .github/workflows/generate-openapi.yml that builds the app, extracts OpenAPI specs, and commits them
    - Location: OpenAPI specs are stored in keruta-doc/common/apiSpec directory
    - Formats: Both JSON and YAML formats are generated

- Removed API authentication requirement
    - Change: Removed token-based authentication from all API endpoints
    - Details: Removed token field from Client struct, removed Authorization headers from all API requests, and updated configuration to not require a token
    - Reason: Simplified API access by removing authentication requirement

- Fixed LoggingFilter order registration issue
    - Problem: Spring Security was not recognizing the @Order annotation on LoggingFilter
    - Solution: Implemented Ordered interface in LoggingFilter class to explicitly register its order
    - Details: Added getOrder() method returning -100 (same as @Order annotation value)
