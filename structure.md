# プロジェクト構造

## コード構造

* /.github/workflows - GitHub Actions ワークフロー設定
  * /build.yml - ビルド、テスト、コードスタイルチェックのワークフロー
  * /docker.yml - Dockerイメージのビルドとプッシュのワークフロー
  * /deploy.yml - Kubernetesへのデプロイワークフロー
* /keruta-api - バックエンドAPIサーバー (サブモジュール)
  * /api - APIサーバーのソースコード
    * /src/main - メインソースコード
    * /src/test - テストコード
  * /core - コアドメインとユースケース
    * /domain - ドメインモデル (Task, Agent, Repository など)
    * /usecase - ビジネスロジックとユースケース
  * /infra - インフラストラクチャ層の実装
    * /app - Kubernetes統合とジョブオーケストレーション
    * /core - インフラストラクチャコア実装
    * /persistence - MongoDBリポジトリ実装
    * /security - セキュリティ関連の実装
      * /config - セキュリティ設定（認証、エラーハンドリングなど）
  * /buildSrc - Gradleビルド設定
  * /gradle - Gradleラッパー
  * /src/db - データベース関連ファイル
    * /migrations - データベースマイグレーションスクリプト
* /keruta-admin - 管理パネルフロントエンド (サブモジュール)
  * /app - フロントエンドアプリケーション
    * /components - UIコンポーネント
    * /routes - ルーティング設定
      * /tasks._index.tsx - タスク一覧ページ
      * /tasks.new.tsx - タスク作成ページ
      * /tasks.edit.$id.tsx - タスク編集ページ
      * /tasks.$id.tsx - タスク詳細ページ
      * /agents._index.tsx - エージェント一覧ページ
      * /documents._index.tsx - ドキュメント一覧ページ
      * /repositories._index.tsx - リポジトリ一覧ページ
      * /kubernetes._index.tsx - Kubernetes設定ページ
      * /sessions._index.tsx - セッション一覧ページ
      * /sessions.new.tsx - セッション作成ページ
      * /sessions.edit.$id.tsx - セッション編集ページ
      * /sessions.$id.tsx - セッション詳細ページ
    * /utils - ユーティリティ関数
      * /api.ts - API通信用ユーティリティ
  * /public - 静的ファイル
* /keruta-agent - タスク実行エージェント (サブモジュール)
  * /cmd - コマンドラインインターフェース
    * /keruta-agent - メインエントリーポイント
  * /internal - 内部実装
    * /api - API通信
    * /commands - コマンド実装
    * /config - 設定
    * /logger - ロギング
  * /pkg - 公開パッケージ
    * /artifacts - 成果物管理
    * /health - ヘルスチェック
  * /scripts - ビルドスクリプトなど
* /keruta-executor - keruta-apiのタスクをcoderで実行するアプリケーション (サブモジュール)
  * /src - ソースコード
    * /main - メインソースコード
      * /kotlin - Kotlinソースコード
        * /net/kigawa/keruta/executor - メインパッケージ
          * /config - 設定
          * /domain - ドメインモデル
          * /service - サービス
            * /CoderExecutionService.kt - coderを使用してタスクを実行するサービス
            * /SshService.kt - SSH経由でコマンドを実行するサービス
            * /TaskApiService.kt - keruta-apiとやり取りするサービス
            * /TaskProcessor.kt - タスク処理を行うサービス
      * /resources - リソースファイル
    * /test - テストコード
* /keruta-doc - プロジェクトドキュメント (https://github.com/kigawa-net/keruta-doc に移行中)
* /kigawa-net-k8s - Kubernetes関連の設定
  * /keruta - Kerutaのデプロイメントマニフェスト
  * /keruta-doc - ドキュメント関連のKubernetes設定

## ドキュメント構造

* /keruta-doc - プロジェクトドキュメント
  * /common - 共通ドキュメント
    * /apiSpec - 自動生成されたOpenAPI仕様書 (GitHub Actionsによって自動的に生成・プッシュ)
  * /keruta - kerutaプロジェクトの設計・運用ドキュメント
    * /admin_panel.md - 管理パネルの機能と使用方法の詳細
    * /auth - 認証・認可関連のドキュメント
    * /git - Git関連のドキュメント
    * /kubernetes - Kubernetes連携関連のドキュメント
    * /kubernetes_integration.md - タスク情報を環境変数としたKubernetes Pod作成の詳細
    * /misc - その他のドキュメント
      * /commonDocuments - 共通ドキュメント
      * /error_handling.md - エラーハンドリングの詳細ドキュメント
      * /logging.md - ログ設定の詳細ドキュメント
    * /project_details.md - セットアップ手順、API仕様、技術スタック、環境変数によるDB設定などの詳細情報
    * /setup - セットアップ関連のドキュメント
    * /system - システム設計関連のドキュメント
      * /api - API仕様関連のドキュメント
    * /task - タスク管理関連のドキュメント
    * /task_queue_system_design.md - コーディングエージェントタスクキューシステムの詳細設計
  * /keruta-admin - keruta管理パネル関連のドキュメント
  * /keruta-agent - kerutaエージェント関連のドキュメント
    * /log-streaming - ログストリーミング関連のドキュメント
  * /keruta-builder - kerutaビルダー関連のドキュメント
  * /keruta-github - kerutaのGitHub連携関連のドキュメント

## 主要なコンポーネント

### コアコンポーネント
1. **Spring Boot APIサーバー** (Kotlin) - メインオーケストレーションサービス
2. **Kerutaエージェント** (Go) - Kubernetesポッドでのタスク実行ランタイム
3. **MongoDB** - 主要データストア
4. **Kubernetes** - コンテナオーケストレーションとジョブ実行

### マルチモジュール構造
- `core:domain` - ドメインモデル (Task, Agent, Repository など)
- `core:usecase` - ビジネスロジックとユースケース
- `infra:persistence` - MongoDBリポジトリ実装
- `infra:security` - セキュリティ設定（現在は許容的）
- `infra:app` - Kubernetes統合とジョブオーケストレーション
- `api` - RESTコントローラーとウェブレイヤー

### 主要なドメインモデル
- **Task**: 実行可能なユニット（ステータス、優先度、Gitリポジトリ関連付けを持つ）
- **Agent**: 言語サポートと現在のタスク割り当てを持つ実行ランタイム
- **Repository**: セットアップスクリプトとストレージ設定を持つGitリポジトリ
- **Document**: タスクに添付できるコンテキストドキュメント
- **Session**: 関連するタスクをグループ化するセッション（ステータス、タグ、メタデータを持つ）
  - SessionStatus: ACTIVE, INACTIVE, COMPLETED, ARCHIVED
  - SessionService: セッションCRUD操作、ステータス更新、タグ管理
  - SessionController: REST API endpoints (/api/v1/sessions)
  - SessionRepository: MongoDB永続化
  - SessionEventListener: セッション・ワークスペースライフサイクルイベント処理
  - SessionWorkspaceStatusSyncService: ワークスペース状態に基づくセッション状態同期
  - CoderWorkspaceMonitoringService: Coder APIからの定期的なワークスペース状態監視
- **Workspace**: セッションごとのCoder風開発環境（ライフサイクル管理、Kubernetesリソース統合）
  - WorkspaceStatus: PENDING, STARTING, RUNNING, STOPPING, STOPPED, DELETING, DELETED, FAILED, CANCELED
  - WorkspaceBuildInfo: ビルド情報とステータス管理
  - WorkspaceResourceInfo: Kubernetesリソース情報（CPU、メモリ、PVC、Pod、Service、Ingress）
- **WorkspaceTemplate**: ワークスペース作成用のテンプレート（パラメータ、設定、バージョン管理）
  - WorkspaceTemplateParameter: テンプレートパラメータ定義
  - WorkspaceParameterType: STRING, NUMBER, BOOLEAN, LIST
