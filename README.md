# keruta

## 概要

このAPIサーバーは、タスクのキューを管理するためのRESTfulなインターフェースを提供します。作成されたタスクは必ずキューに登録され、優先順位に基づいて処理されます。

## 機能

- タスクの作成、読取、更新、削除(CRUD操作)
- タスクの自動キュー登録と取得
- タスクの優先順位付け
- タスクのステータス管理
- ドキュメントの保存機能
- GitリポジトリのURL保存機能
- JWT認証によるセキュアなアクセス制御

## 技術スタック

- Java 21
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
    ├── task/                   # タスク管理API
    ├── document/               # ドキュメント管理API
    └── repository/             # Gitリポジトリ管理API
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

### Gitリポジトリ管理

```
GET    /api/v1/repositories       # リポジトリ一覧の取得
POST   /api/v1/repositories       # 新規リポジトリURLの登録
GET    /api/v1/repositories/{id}  # 特定リポジトリ情報の取得
PUT    /api/v1/repositories/{id}  # リポジトリ情報の更新
DELETE /api/v1/repositories/{id}  # リポジトリ情報の削除
GET    /api/v1/repositories/{id}/validate # リポジトリURLの有効性確認
```
