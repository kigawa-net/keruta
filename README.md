# keruta

## 概要

このAPIサーバーは、タスクのキューを管理するためのRESTfulなインターフェースを提供します。作成されたタスクは必ずキューに登録され、優先順位に基づいて処理されます。

## セットアップと実行方法

### 必要条件
- Java 21
- Docker と Docker Compose (MongoDBのセットアップ用)

### MongoDBのセットアップ
```bash
# docker-composeを使用してMongoDBを起動
docker-compose up -d
```

初期認証情報:
- ユーザー名: admin
- パスワード: password

### ビルドと実行
```bash
# プロジェクトのビルド
./gradlew build

# アプリケーションの実行
./gradlew bootRun
```

アプリケーションは http://localhost:8080 で起動します。

### 動作確認
```bash
# ヘルスチェック
curl http://localhost:8080/api/health

# タスク一覧の取得
curl http://localhost:8080/api/v1/tasks
```

### 管理パネルとAPI仕様書

アプリケーションを起動後、以下のURLでアクセスできます：

- 管理パネル: http://localhost:8080/admin (ログインが必要)
- Swagger UI (API仕様書): http://localhost:8080/swagger-ui.html

## 機能

- タスクの作成、読取、更新、削除(CRUD操作)
- タスクの自動キュー登録と取得
- タスクの優先順位付け
- タスクのステータス管理
- ドキュメントの保存機能
- JWT認証によるセキュアなアクセス制御
- ログインは環境変数のtokenで行う
- Swagger/OpenAPIによるAPI仕様書の自動生成
- 簡易的な管理パネル（タスク、ドキュメントの管理）

## 技術スタック

- Kotlin
- Spring Boot
- MongoDB
- Gradle (buildSrcによるマルチモジュール構成)

## プロジェクト構造

```
keruta/
├── buildSrc/                    # ビルド構成の共通化
│   └── src/main/kotlin/        # ビルドロジック
├── core/                       # コアモジュール
│   ├── domain/                 # ドメインモデル
│   └── usecase/                # ユースケース実装
├── infra/                      # インフラストラクチャレイヤー
│   ├── persistence/            # MongoDB永続化実装
│   └── security/               # JWT認証実装
└── api/                        # APIモジュール
```

## API エンドポイント

### 認証・認可

```
POST   /api/v1/auth/login         # ログイン処理、JWTトークンを返却
POST   /api/v1/auth/refresh       # JWTトークンのリフレッシュ
```

### タスク管理

```
GET    /api/v1/tasks              # タスク一覧の取得
POST   /api/v1/tasks              # 新規タスクの作成（自動的にキューに追加）
GET    /api/v1/tasks/{id}         # 特定タスクの取得
PUT    /api/v1/tasks/{id}         # タスクの更新
DELETE /api/v1/tasks/{id}         # タスクの削除
GET    /api/v1/tasks/queue/next   # キューから次のタスクを取得
PATCH  /api/v1/tasks/{id}/status  # タスクのステータス更新
PATCH  /api/v1/tasks/{id}/priority # タスクの優先度更新
```

### ドキュメント管理

```
GET    /api/v1/documents          # ドキュメント一覧の取得
POST   /api/v1/documents          # 新規ドキュメントの作成
GET    /api/v1/documents/{id}     # 特定ドキュメントの取得
PUT    /api/v1/documents/{id}     # ドキュメントの更新
DELETE /api/v1/documents/{id}     # ドキュメントの削除
GET    /api/v1/documents/search   # ドキュメントの検索
```

