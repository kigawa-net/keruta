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
* /keruta-executor - keruta-apiのタスクをCoderワークスペースで実行するオーケストレーター (独立モジュール)
  * /src - ソースコード
    * /main - メインソースコード
      * /kotlin - Kotlinソースコード
        * /net/kigawa/keruta/executor - メインパッケージ
          * /config - 設定クラス
            * /KerutaExecutorProperties.kt - アプリケーション設定
            * /RestTemplateConfig.kt - HTTP通信設定
            * /SchedulingConfig.kt - スケジューリング設定
            * /WebClientConfig.kt - Webクライアント設定
          * /domain - ドメインモデル
            * /model/Task.kt - タスクデータモデル
            * /model/TaskScript.kt - タスクスクリプトモデル
          * /service - サービス層
            * /CoderExecutionService.kt - Coderワークスペースでのタスク実行
            * /LocalExecutionService.kt - ローカル実行サービス（開発用）
            * /SessionMonitoringService.kt - セッション状態監視サービス
            * /TaskApiService.kt - keruta-apiとのHTTP通信サービス
            * /TaskProcessor.kt - メインタスク処理サービス（定期実行）
      * /resources - リソースファイル
        * /application.properties - アプリケーション設定ファイル
    * /test - テストコード（現在未実装）
  * /build.gradle.kts - Gradleビルド設定
  * /docker-compose.yml - 開発用Docker構成
  * /Dockerfile - コンテナイメージビルド設定
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
   - Spring CGLIBプロキシ対応済み（全@Serviceクラスをopen化）
   - セッション・ワークスペース・タスク管理
2. **Kerutaエージェント** (Go) - Kubernetesポッドでのタスク実行ランタイム
   - Cobra CLIフレームワーク、HTTP通信、ログストリーミング
3. **Keruta Executor** (Kotlin) - タスク実行オーケストレーター
   - APIからのタスク取得とCoderワークスペースでの実行
4. **Remix管理パネル** (TypeScript/React) - Webベース管理インターフェース
5. **MongoDB** - 主要データストア（タスク、セッション、ワークスペース等）
6. **Kubernetes** - コンテナオーケストレーションとジョブ実行
7. **Coder** - ワークスペース管理システム（REST API統合）
8. **PostgreSQL** - Keycloak認証システム用DB
9. **Keycloak** - 認証・認可システム

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
- **Session**: 関連するタスクとワークスペースをグループ化するセッション
  - SessionStatus: ACTIVE, INACTIVE, COMPLETED, ARCHIVED
  - SessionTemplateConfig: Coderテンプレート設定、パラメータ管理
  - SessionService: セッションCRUD操作、ステータス更新、タグ管理
  - SessionController: REST API endpoints (/api/v1/sessions)
  - SessionRepository: MongoDB永続化
  - SessionEventListener: セッション・ワークスペースライフサイクルイベント処理
    - ワークスペース名の自動正規化機能（日本語→Coder互換形式）
  - SessionWorkspaceStatusSyncService: ワークスペース状態に基づくセッション状態同期
  - CoderWorkspaceMonitoringService: Coder APIからの定期的なワークスペース状態監視（2分間隔）
- **Workspace**: セッションと1対1関係のCoderワークスペース（完全管理型開発環境）
  - **1対1関係**: 各セッションに対して1つのワークスペースのみ存在
  - 自動ライフサイクル: セッション作成→ワークスペース作成→自動同期→自動削除
  - WorkspaceStatus: PENDING, STARTING, RUNNING, STOPPING, STOPPED, DELETING, DELETED, FAILED, CANCELED
  - WorkspaceBuildInfo: ビルド情報とステータス管理（ビルドID、ログ、理由）
  - WorkspaceResourceInfo: Kubernetesリソース情報（CPU、メモリ、PVC、Pod、Service、Ingress）
  - WorkspaceService: ワークスペースのCRUD操作、Coder API通信
  - WorkspaceOrchestrator: ワークスペースライフサイクル管理
  - WorkspaceKubernetesHandler: Kubernetesリソースとの統合（TODO: 未実装）
- **WorkspaceTemplate**: ワークスペース作成用のテンプレート（パラメータ、設定、バージョン管理）
  - WorkspaceTemplateParameter: テンプレートパラメータ定義
  - WorkspaceParameterType: STRING, NUMBER, BOOLEAN, LIST

## 最新の技術的改善

### Spring Boot統合強化 (2025-07-20)
- **CGLIBプロキシ対応**: 全ての`@Service`クラスに`open`修飾子を追加
- **依存性注入エラー修正**: Kotlinのデフォルト`final`クラス問題を解決
- **AOP機能サポート**: SpringのAspect Oriented Programming機能が正常動作

### Coder統合改善 (2025-07-20)
- **ワークスペース名正規化**: 日本語・特殊文字の自動変換機能
- **バリデーション準拠**: Coderの命名規則（英数字+ハイフン、32文字以下）に対応
- **国際化対応**: 多言語セッション名でもCoder APIで正常動作
- **自動フォールバック**: 不正な名前の場合は"workspace"にフォールバック

### コード品質向上
- **型安全性**: Kotlinの`open`修飾子による適切な継承制御
- **エラーハンドリング**: Coder API通信エラーの詳細ログ記録
- **保守性**: ワークスペース名正規化ロジックの独立実装
