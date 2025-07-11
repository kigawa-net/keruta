# Todo List

- keruta-docリポジトリへの完全な移行

## Completed Tasks

- apiから管理パネルを削除
    - Change: APIサーバーから管理パネル機能を削除
    - Details: AdminController、TaskAdminController、AgentAdminController、DocumentAdminController、KubernetesAdminController、RepositoryAdminControllerなどの管理パネル関連のコントローラーを削除し、RootControllerを更新
    - Location: /keruta-api/api/src/main/kotlin/net/kigawa/keruta/api/
    - Reason: 管理パネル機能をkeruta-adminモジュールに移行するため

- adminに新規タスク作成機能追加
    - Change: keruta-adminモジュールに新規タスク作成機能を追加
    - Details: 新規タスク作成フォームのルート(tasks.new.tsx)を作成し、タスク一覧ページの「新規タスク作成」ボタンに機能を追加
    - Location: /keruta-admin/app/routes/tasks.new.tsx, /keruta-admin/app/routes/tasks._index.tsx
    - Reason: ユーザーが管理パネルから新しいタスクを作成できるようにするため

- バックエンドの実装をkeruta-apiとkeruta-adminに移動
    - Change: バックエンドの実装をkeruta-apiサブモジュールに移動し、keruta-adminサブモジュールと連携
    - Details: api、core、infraディレクトリの内容をkeruta-apiサブモジュールに移動し、プロジェクト構造を更新
    - Location: /keruta-api、/keruta-admin
    - Reason: プロジェクト構造を改善し、バックエンドとフロントエンドを分離するため

- Fixed health endpoint path
    - Change: Updated the health endpoint path from /api/health to /api/v1/health
    - Details: Modified the RequestMapping annotation in HealthController
    - Location: /api/src/main/kotlin/net/kigawa/keruta/api/v1/HealthController.kt
    - Reason: To match the expected path in tests and GitHub workflow

- Fixed task status update endpoint to handle message field
    - Change: Modified TaskController to properly handle the message field in task status update requests
    - Details: Updated the updateTaskStatus method to extract the message from the request and update the task's description field
    - Location: /api/src/main/kotlin/net/kigawa/keruta/api/task/controller/TaskController.kt
    - Reason: To fix a 400 error that occurred when the client included a message field in the request

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

- Improved keruta-agent implementation
    - Change: Enhanced input handling and API client implementation
    - Details: Added HTTPClient implementation with HTTP polling for input, updated documentation to reflect changes
    - Location: /keruta-agent/internal/api/http_client.go, /keruta-agent/README.md
    - Reason: Improved robustness in Kubernetes environments where standard input might not be available

- Fixed LoggingFilter order registration issue
    - Problem: Spring Security was not recognizing the @Order annotation on LoggingFilter
    - Solution: Implemented Ordered interface in LoggingFilter class to explicitly register its order
    - Details: Added getOrder() method returning -100 (same as @Order annotation value)

- Changed HTTP input polling interval to 5 seconds
    - Change: Modified the polling interval for HTTP input from 10 seconds to 5 seconds
    - Details: Updated all sleep durations in client.go, http_client.go, and input.go, and updated comments to reflect the new maximum polling time
    - Location: /keruta-agent/internal/api/client.go, /keruta-agent/internal/api/http_client.go, /keruta-agent/internal/api/input.go
    - Reason: To make the agent poll the server for input every 5 seconds as specified in the requirements
