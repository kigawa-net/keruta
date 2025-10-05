# keruta

## 概要

Kerutaは、Kubernetes-native なタスク実行システムです。タスクのキューを管理するRESTfulなインターフェース、Coderのワークスペース管理機能、セッションベースの開発環境統合を提供します。タスクは優先順位に基づいて処理され、各セッションに対し1対1でワークスペースが自動作成されます。

## プロジェクト構成

このプロジェクトは以下のサブモジュールで構成されています：

- **keruta-api**: バックエンドAPIサーバー（Spring Boot + Kotlin）
- **keruta-admin**: 管理パネルフロントエンド（Remix + React + TypeScript）
- **keruta-agent**: タスク実行エージェント（Kotlin）
- **keruta-executor**: タスク実行オーケストレーター（Spring Boot + Kotlin）
- **keruta-doc**: プロジェクトドキュメント（独立リポジトリに移行中）

## クイックスタート

ローカルでの実行は非推奨

## 主な機能

- タスクの作成、読取、更新、削除(CRUD操作)
- タスクの自動キュー登録と優先順位付け
- タスクの統合管理
- タスクに複数のドキュメントを関連付け可能
- タスクにリポジトリを関連付け可能
- ドキュメントとGitリポジトリの管理
- Kubernetesとの統合（タスク情報を環境変数としたPod作成）
- Kubernetesのデフォルト設定をデータベースに保存
- **セッション管理**
  - セッションの作成、詳細表示、編集、削除機能
  - セッションステータス管理（ACTIVE, INACTIVE, COMPLETED, ARCHIVED）
  - セッションのタグ付けと検索機能
  - セッションによる関連タスクのグループ化
  - **Coderワークスペース状態に基づく自動ステータス同期**
    - 定期的なワークスペース状態監視（2分間隔）
    - ワークスペース状態変更に基づくセッション状態自動更新
    - 手動同期エンドポイント（POST /api/v1/sessions/{id}/sync-status）
    - Coder APIからの状態取得と同期（POST /api/v1/sessions/{id}/monitor-workspaces）
  - REST API（GET/POST/PUT/DELETE /api/v1/sessions）
- **Coderワークスペース機能**
  - **セッションとワークスペースの1対1関係**
    - 各セッションに対し1つのワークスペースが自動作成・管理
    - セッション作成時の自動ワークスペース生成
    - セッション削除時の自動ワークスペースクリーンアップ
    - 日本語セッション名のCoder互換ワークスペース名への自動正規化
    - Coderワークスペース状態に基づく自動ステータス同期（2分間隔監視）
  - ワークスペースのライフサイクル管理（作成、開始、停止、削除）
  - ワークスペーステンプレート管理とパラメータ設定
  - Coder REST APIとの統合による完全なワークスペース管理
  - セッション詳細でのワークスペースリンク表示とリアルタイム状態更新
  - ワークスペースの状態監視とビルド情報管理
  - Kubernetesリソース統合（PVC、Pod、Service、Ingress）
- **タスク実行オーケストレーション（keruta-executor）**
  - タスクキューからの自動タスク取得と実行
  - Coderワークスペース内でのタスク実行
  - セッション監視とワークスペース状態同期
  - API経由でのタスクステータス更新
- 管理パネル（/admin からアクセス可能）
  - タスクの作成、詳細表示、編集、削除機能
  - エージェント、ドキュメント、リポジトリの管理
  - セッションとワークスペースの管理
- ヘルスチェックエンドポイント（/api/health および /api/v1/health）
- 丁寧で詳細なエラーレスポンス（ユーザーフレンドリーなエラーメッセージ）

## 詳細ドキュメント

詳細な情報は以下のドキュメントを参照してください：

- [プロジェクト詳細](keruta-doc/keruta/project_details.md) - セットアップ手順、API仕様、技術スタック、環境変数によるDB設定などの詳細情報
- [管理パネル](keruta-doc/keruta/admin_panel.md) - 管理パネルの機能と使用方法の詳細
- [Kubernetes統合](keruta-doc/keruta/kubernetes_integration.md) - タスク情報を環境変数としたKubernetes Pod作成の詳細
- [タスクキューシステム設計](keruta-doc/keruta/task_queue_system_design.md) - コーディングエージェントタスクキューシステムの詳細設計
- [ログ設定](keruta-doc/keruta/misc/logging.md) - アプリケーションのログ設定と詳細なログ出力の説明
- [エラーハンドリング](keruta-doc/keruta/misc/error_handling.md) - アプリケーションのエラーレスポンス形式と例外処理の詳細
- [API仕様](keruta-doc/common/apiSpec) - 自動生成されたOpenAPI仕様書（JSON/YAML形式）

## 技術スタック

### バックエンド
- **Kotlin** - 主要開発言語（Spring Boot API、Executor）
- **Go 1.24** - エージェント実装（Cobra CLI、logrus）
- **Spring Boot 3.x** - APIサーバーフレームワーク
- **MongoDB** - メインデータストア
- **Gradle 8.13** - ビルドツール（マルチモジュール構成）

### フロントエンド
- **TypeScript** - 型安全な開発
- **Remix 2.3** - フルスタックReactフレームワーク
- **React 18** - UIライブラリ
- **Bootstrap 5.3** - UIコンポーネント

### インフラストラクチャ
- **Docker & Docker Compose** - コンテナ化と開発環境
- **Kubernetes** - 本番環境でのコンテナオーケストレーション
- **Coder** - ワークスペース管理システム
- **MongoDB** - ドキュメント指向データベース
- **PostgreSQL** - Keycloak認証用データベース
- **Keycloak** - 認証・認可システム（設定済み）

### 開発・運用
- **GitHub Actions** - CI/CDパイプライン
- **ktlint** - Kotlinコードフォーマッター
- **Harbor Registry** - プライベートコンテナレジストリ
- **TestContainers** - 統合テスト環境

## 最近の更新

### Spring Boot CGLIBプロキシ対応 (2025-07-20)

- Kotlinクラスのデフォルト`final`修飾子によるSpring依存性注入エラーを修正
- 全ての`@Service`クラスに`open`修飾子を追加してCGLIBプロキシ作成を可能化
- SpringのAOP機能が正常に動作するよう改善

### Coderワークスペース名正規化 (2025-07-20)

- 日本語やその他の特殊文字を含むセッション名をCoder互換の形式に自動変換
- ワークスペース名のバリデーションルール準拠（英数字とハイフンのみ、32文字以下）
- セッション作成時の自動正規化処理により、国際化対応を改善

## コード品質

### Linter (ktlint)

このプロジェクトでは、Kotlinコードの品質を保つためにktlintを使用しています。

#### 使用方法

コードのチェック:
```bash
./gradlew ktlintCheck        # 単一モジュールのチェック
./gradlew ktlintCheckAll     # すべてのモジュールをチェック
```

コードの自動フォーマット:
```bash
./gradlew ktlintFormat       # 単一モジュールのフォーマット
./gradlew ktlintFormatAll    # すべてのモジュールをフォーマット
```

#### 設定

- ktlintの設定は`.editorconfig`ファイルで管理されています
- IDEの設定を`.editorconfig`に合わせることで、コーディング中に一貫したスタイルを維持できます

#### 現在の設定について

現在、以下のルールは段階的な移行を容易にするために無効化されています：
- `ktlint_standard_no-wildcard-imports`: ワイルドカードインポートを禁止するルール
- `ktlint_standard_filename`: ファイル名をクラス名と一致させるルール
- `ktlint_standard_max-line-length`: 行の長さを制限するルール

これらのルールは将来的に有効化される予定です。新しいコードを書く際は、これらのルールに従うことをお勧めします。

## CI/CD

このプロジェクトではGitHub Actionsを使用して継続的インテグレーション/継続的デリバリー（CI/CD）を実現しています。

### ワークフロー

以下のワークフローが設定されています：

1. **ビルドとテスト** - コードのビルド、テスト、リントチェックを行います
   - mainブランチへのプッシュとプルリクエストで実行されます
   - JDK 21を使用してGradleでビルドします
   - 単体テストを実行します
   - ktlintでコードスタイルをチェックします

2. **Dockerイメージのビルドとプッシュ** - 各モジュールのDockerイメージを作成します
   - ビルドとテストが成功した後、mainブランチでのみ実行されます
   - keruta-apiとkeruta-executorのDockerイメージをビルドします
   - イメージをHarborレジストリ（harbor.kigawa.net）にプッシュします

3. **Kubernetesへのデプロイ** - アプリケーションをKubernetesクラスタにデプロイします
   - Dockerイメージのビルドとプッシュが成功した後、mainブランチでのみ実行されます
   - 最新のイメージタグでデプロイメント設定を更新します
   - kubectlを使用してKubernetesマニフェストを適用します
   - デプロイメントのロールアウトステータスを確認します

### 必要なシークレット

GitHub Actionsワークフローを実行するには、以下のシークレットをリポジトリに設定する必要があります：

- `HARBOR_USERNAME`: Harborレジストリのユーザー名
- `HARBOR_PASSWORD`: Harborレジストリのパスワード
- `KUBE_CONFIG`: Kubernetesクラスタへのアクセスに必要なkubeconfigファイル
