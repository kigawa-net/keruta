# Todo List

* 管理パネル
    * リポジトリ管理実装
    * k8s管理実装
    * エージェント管理実装

## Completed Tasks

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
