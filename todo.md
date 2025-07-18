# Todo List
* リンター設定
* 管理パネル
    * リポジトリ管理実装
    * k8s管理実装
    * エージェント管理実装
    * セッション管理実装
    * ワークスペース管理実装
* ワークスペース機能
    * KubernetesハンドラーのTODO実装
    * ワークスペースの実際のKubernetesリソース作成
    * ワークスペースとタスクの統合
    * ワークスペースのログストリーミング
    * ワークスペースのファイル管理API
* テンプレート機能
    * デフォルトテンプレートの作成
    * カスタムテンプレートの作成機能
    * テンプレートパラメータの動的検証

## Completed Tasks

- Coder統合のビルドエラー修正
    - Change: CoderApiClientの実装をinfrastructure層に移動し、Jackson依存関係とsuspend関数の問題を修正してビルドエラーを解決
    - Details: CoderApiClientImplをinfra.app.coderパッケージに作成し、JsonPropertyアノテーション付きのDTOクラスでAPIマッピングを実装。usecase層からJackson依存関係を削除し、Clean Architectureの原則に準拠
    - Location: /keruta-api/infra/app/src/main/kotlin/net/kigawa/keruta/infra/app/coder/, /keruta-api/core/usecase/src/main/kotlin/net/kigawa/keruta/core/usecase/coder/CoderModels.kt
    - Reason: Clean Architectureの原則に従い、外部依存関係をインフラストラクチャ層に分離してビルドエラーを解決するため

- coderのrest apiを利用してsession詳細にworkspaceリンクを記載する機能の実装
    - Change: Coder REST APIを利用してworkspace管理を行い、session詳細にworkspaceへのリンクを表示する機能を実装
    - Details: CoderApiClient、CoderService、WorkspaceKubernetesHandlerの実装でCoder APIとの統合を完了。SessionResponseにworkspace情報を追加し、SessionDetailControllerでワークスペースリンクを含む詳細情報を提供
    - Location: /keruta-api/core/usecase/src/main/kotlin/net/kigawa/keruta/core/usecase/coder/, /keruta-api/api/src/main/kotlin/net/kigawa/keruta/api/session/controller/SessionDetailController.kt, /keruta-api/api/src/main/kotlin/net/kigawa/keruta/api/session/dto/SessionDetailResponse.kt
    - Reason: Coder REST APIを活用してworkspace管理を効率化し、sessionからworkspaceへの直接アクセスを可能にするため

- sessionごとにcoderのworkspaceを作成する機能の実装
    - Change: sessionごとにCoder風のworkspaceを作成する機能を実装
    - Details: Workspaceドメインモデル、WorkspaceService、WorkspaceRepository、WorkspaceController、Persistence層を実装し、SessionとWorkspaceの関連付け機能を追加。API endpoints、DTOs、エンティティも作成
    - Location: /keruta-api/core/domain/src/main/kotlin/net/kigawa/keruta/core/domain/model/Workspace.kt, /keruta-api/core/usecase/src/main/kotlin/net/kigawa/keruta/core/usecase/workspace/, /keruta-api/api/src/main/kotlin/net/kigawa/keruta/api/workspace/, /keruta-api/infra/persistence/src/main/kotlin/net/kigawa/keruta/infra/persistence/entity/WorkspaceEntity.kt
    - Reason: CoderのようなworkspaceライフサイクルをKerutaに統合し、sessionごとに独立した開発環境を提供するため

- ドキュメント管理実装
    - Change: keruta-adminモジュールにドキュメント管理機能を実装
    - Details: ドキュメントの一覧表示、詳細表示、新規作成、編集、削除、検索、タグフィルタリング機能を実装
    - Location: /keruta-admin/app/routes/documents._index.tsx, /keruta-admin/app/routes/documents.new.tsx, /keruta-admin/app/routes/documents.$id.tsx, /keruta-admin/app/routes/documents.edit.$id.tsx
    - Reason: ユーザーが管理パネルからドキュメントを管理できるようにするため

- adminにtask削除ボタン実装、task編集ボタン実装、task詳細ボタン実装
    - Change: keruta-adminモジュールにタスク削除、編集、詳細表示機能を追加
    - Details: タスク一覧ページに削除、編集、詳細ボタンの機能を実装し、タスク編集ページ(
      tasks.edit.$id.tsx)とタスク詳細ページ(tasks.$id.tsx)を作成
    - Location: /keruta-admin/app/routes/tasks._index.tsx,
      /keruta-admin/app/routes/tasks.edit.$id.tsx, /keruta-admin/app/routes/tasks.$id.tsx
    - Reason: ユーザーが管理パネルからタスクを管理できるようにするため

- fix err
    - Change: selectタグのselected属性をdefaultValueプロパティに変更
    - Details: React警告「Use the `defaultValue` or `value` props on <select> instead of setting」を修正
    - Location: /keruta-admin/app/routes/tasks.new.tsx
    - Reason: Reactのベストプラクティスに従い、非制御コンポーネントでは属性ではなくdefaultValueプロパティを使用するため

- SSHをcoderのAPIを利用して自動で構成する機能の追加
    - Change: keruta-executorにSSH設定を自動的に構成する機能を追加
    - Details: SshServiceを更新して、タスクスクリプトの環境変数からSSH設定を抽出し、自動的に構成する機能を実装。README.mdを更新して環境変数によるSSH自動設定について説明を追加
    - Location: /keruta-executor/src/main/kotlin/net/kigawa/keruta/executor/service/SshService.kt,
      /keruta-executor/README.md, /todo.md
    - Reason: coderのAPIを利用してSSH設定を自動的に構成することで、より柔軟なタスク実行環境を提供するため

- keruta-executorのcoderコマンドの実装とSSH機能の追加
    - Change: keruta-executorにSSH経由でコマンドを実行する機能を追加
    - Details:
      SshServiceを作成してSSH経由でコマンドを実行する機能を実装し、CoderExecutionServiceを更新してSSHを使用するように変更。また、SSH接続の設定プロパティを追加し、ドキュメントを更新
    - Location: /keruta-executor/src/main/kotlin/net/kigawa/keruta/executor/service/SshService.kt,
      /keruta-executor/src/main/kotlin/net/kigawa/keruta/executor/service/CoderExecutionService.kt,
      /keruta-executor/src/main/kotlin/net/kigawa/keruta/executor/config/KerutaExecutorProperties.kt,
      /keruta-executor/src/main/resources/application.properties, /keruta-executor/README.md, /structure.md, /todo.md
    - Reason: SSH経由でコマンドを実行することで、より柔軟なタスク実行環境を提供するため

- GitHub Actionsによる継続的インテグレーション/継続的デリバリー（CI/CD）の実装
    - Change: GitHub Actionsワークフローを作成してCI/CDパイプラインを実装
    - Details: ビルド・テスト用、Dockerイメージビルド用、Kubernetesデプロイ用の3つのワークフローを作成し、README.mdを更新してCI/CDについて説明を追加
    - Location: /.github/workflows/build.yml, /.github/workflows/docker.yml, /.github/workflows/deploy.yml, /README.md,
      /structure.md
    - Reason: 自動化されたビルド、テスト、デプロイプロセスを実現し、開発効率と品質を向上させるため

- keruta-executorのドキュメントを日本語に翻訳
    - Change: keruta-executorのREADME.mdを日本語に翻訳
    - Details: 英語で書かれていたREADME.mdの内容を日本語に翻訳し、日本語ユーザーが理解しやすいようにした
    - Location: /keruta-executor/README.md
    - Reason: 日本語ユーザーがドキュメントを理解しやすくするため

- keruta-executorにkeruta-apiのタスクをcoderで実行するアプリケーションを実装
    - Change: keruta-executorにKotlinでタスク実行アプリケーションを実装
    - Details: Spring
      Bootアプリケーションとして、TaskProcessor、TaskApiService、CoderExecutionServiceを実装し、keruta-apiからタスクを取得して実行する機能を追加
    - Location: /keruta-executor/src/main/kotlin/net/kigawa/keruta/executor/
    - Reason: keruta-apiのタスクをcoderで実行するため

- apiから管理パネルを削除
    - Change: APIサーバーから管理パネル機能を削除
    - Details:
      AdminController、TaskAdminController、AgentAdminController、DocumentAdminController、KubernetesAdminController、RepositoryAdminControllerなどの管理パネル関連のコントローラーを削除し、RootControllerを更新
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
    - Details: Updated the updateTaskStatus method to extract the message from the request and update the task's
      description field
    - Location: /api/src/main/kotlin/net/kigawa/keruta/api/task/controller/TaskController.kt
    - Reason: To fix a 400 error that occurred when the client included a message field in the request
