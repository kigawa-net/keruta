# keruta

## 概要

このAPIサーバーは、タスクのキューを管理するためのRESTfulなインターフェースを提供します。作成されたタスクは必ずキューに登録され、優先順位に基づいて処理されます。

## クイックスタート

### 方法1: Gradleで実行

```bash
# MongoDBの起動
docker compose up -d mongodb

# アプリケーションの実行
./gradlew bootRun
```

### 方法2: Dockerで実行

```bash
# アプリケーションとMongoDBの起動
docker compose up -d
```

アプリケーションは http://localhost:8080 で起動します。

**注意**: Docker Composeで起動すると、Keycloakに「keruta」レルムが自動的に作成されます。
- Keycloakの設定は data/keycloak.default.realm.json から自動的にインポートされます
- 必要に応じて、Keycloak管理コンソール（http://localhost:8180/admin/）にアクセスできます（ユーザー名: admin、パスワード: admin）
- 詳細な設定については [Keycloak統合](keruta-doc/keruta/keycloak_integration.md) を参照してください

## 主な機能

- タスクの作成、読取、更新、削除(CRUD操作)
- タスクの自動キュー登録と優先順位付け
- タスクの統合管理
- タスクに複数のドキュメントを関連付け可能
- タスクにリポジトリを関連付け可能
- ドキュメントとGitリポジトリの管理
- JWT認証とKeycloak統合によるセキュアなアクセス制御
- Kubernetesとの統合（タスク情報を環境変数としたPod作成）
- Kubernetesのデフォルト設定をデータベースに保存
- 管理パネル（/admin からアクセス可能）

## 詳細ドキュメント

詳細な情報は以下のドキュメントを参照してください：

- [プロジェクト詳細](keruta-doc/keruta/project_details.md) - セットアップ手順、API仕様、技術スタック、環境変数によるDB設定などの詳細情報
- [管理パネル](keruta-doc/keruta/admin_panel.md) - 管理パネルの機能と使用方法の詳細
- [Kubernetes統合](keruta-doc/keruta/kubernetes_integration.md) - タスク情報を環境変数としたKubernetes Pod作成の詳細
- [Keycloak統合](keruta-doc/keruta/keycloak_integration.md) - Keycloakを使用した認証・認可の設定詳細
- [タスクキューシステム設計](keruta-doc/keruta/task_queue_system_design.md) - コーディングエージェントタスクキューシステムの詳細設計
- [ログ設定](keruta-doc/keruta/misc/logging.md) - アプリケーションのログ設定と詳細なログ出力の説明

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
