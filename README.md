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

## 主な機能

- タスクの作成、読取、更新、削除(CRUD操作)
- タスクの自動キュー登録と優先順位付け
- ドキュメントとGitリポジトリの管理
- JWT認証によるセキュアなアクセス制御
- Kubernetesとの統合（タスク情報を環境変数としたPod作成）

## 詳細ドキュメント

詳細な情報は以下のドキュメントを参照してください：

- [プロジェクト詳細](doc/project_details.md) - セットアップ手順、API仕様、技術スタック、環境変数によるDB設定などの詳細情報
- [Kubernetes統合](doc/kubernetes_integration.md) - タスク情報を環境変数としたKubernetes Pod作成の詳細

## 技術スタック

- Kotlin
- Spring Boot
- MongoDB
- Gradle (マルチモジュール構成)
