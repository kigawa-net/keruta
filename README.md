# keruta

## 概要

このAPIサーバーは、タスクのキューを管理するためのRESTfulなインターフェースを提供します。作成されたタスクは必ずキューに登録され、優先順位に基づいて処理されます。

## プロジェクト構成

このプロジェクトは以下のサブモジュールで構成されています：

- **keruta-api**: バックエンドAPIサーバー
- **keruta-admin**: 管理パネルフロントエンド
- **keruta-agent**: タスク実行エージェント
- **keruta-executor**: タスク実行エンジン
- **keruta-doc**: プロジェクトドキュメント

## クイックスタート

### 方法1: Gradleで実行

```bash
# keruta-apiディレクトリに移動
cd keruta-api

# MongoDBの起動
docker compose up -d mongodb

# アプリケーションの実行
./gradlew :api:bootRun
```

### 方法2: Dockerで実行

```bash
# アプリケーションとMongoDBの起動
docker compose up -d
```

アプリケーションは http://localhost:8080 で起動します。

## 主な機能

- タスクの作成、読取、更新、削除(CRUD操作)
- タスクの自動キュー登録と優先順位付け
- タスクの統合管理
- タスクに複数のドキュメントを関連付け可能
- タスクにリポジトリを関連付け可能
- ドキュメントとGitリポジトリの管理
- Kubernetesとの統合（タスク情報を環境変数としたPod作成）
- Kubernetesのデフォルト設定をデータベースに保存
- 管理パネル（/admin からアクセス可能）
  - タスクの作成、詳細表示、編集、削除機能
  - エージェント、ドキュメント、リポジトリの管理
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

- Kotlin
- Spring Boot
- MongoDB
- Gradle (マルチモジュール構成)

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
