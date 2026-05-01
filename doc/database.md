# Database

## アーキテクチャ

### 三層永続化抽象化

```
PersisterSession（インターフェース）
  └── verify(authRequestMsg): Res<AuthenticatedPersisterSession>

AuthenticatedPersisterSession（インターフェース）
  ├── createTask(task): Res<Unit>
  └── getProviders(): Res<List<PersistedProvider>>

DbPersisterSession（KTSE実装）
  └── DbAuthenticatedPersisterSession
      ├── タスク作成（TODO: 未実装）
      └── プロバイダー取得
```

## スキーマ

### ER図

```
user (1) ─────< (N) user_idp
  │
  ├─────< (N) provider
  │              │
  ├─────< (N) queue_user >──── (N) queue
  │                                  │
  └─────< (N) task >───────────────< (N)
```

### テーブル定義

**user** — システムユーザー

| カラム | 型 | 説明 |
|---|---|---|
| id | BIGINT PK | |
| create_at | TIMESTAMP | |

**user_idp** — ユーザーとIdPの関係

| カラム | 型 | 説明 |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK→user | |
| subject | VARCHAR | IdPサブジェクト |
| issuer | VARCHAR | IdP発行者URL |
| create_at | TIMESTAMP | |

UNIQUE(subject, issuer)

**provider** — 外部認証プロバイダー

| カラム | 型 | 説明 |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK→user | |
| issuer | VARCHAR | |
| audience | VARCHAR | |
| name | VARCHAR | |
| create_at | TIMESTAMP | |

UNIQUE(user_id, issuer, audience)

**queue** — タスクキュー

| カラム | 型 | 説明 |
|---|---|---|
| id | BIGINT PK | |
| provider_id | BIGINT FK→provider | |
| name | VARCHAR | |
| create_at | TIMESTAMP | |

**queue_user** — キュー↔ユーザー 多対多

| カラム | 型 | 説明 |
|---|---|---|
| id | BIGINT PK | |
| queue_id | BIGINT FK→queue | |
| user_id | BIGINT FK→user | |
| create_at | TIMESTAMP | |

UNIQUE(queue_id, user_id)

**task** — タスク

| カラム | 型 | 説明 |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK→user | |
| queue_id | BIGINT FK→queue | |
| name | VARCHAR | |
| create_at | TIMESTAMP | |

## マイグレーション（Flyway）

マイグレーションファイル: `ktse/src/main/resources/db/migration/`

```
V001__create_user_table.sql
V002__create_user_idp_table.sql
V003__create_provider_table.sql
V004__create_queue_table.sql
V005__create_queue_user_table.sql
V006__create_task_table.sql
```

アプリケーション起動時に自動実行。

## 接続設定（HikariCP）

環境変数:

| 変数 | デフォルト |
|---|---|
| DB_JDBC_URL | jdbc:mysql://localhost:3306/keruta |
| DB_USERNAME | keruta |
| DB_PASSWORD | keruta |

最大プールサイズ: 10

## データベースアクセス

`DbPersister.execTransaction<T>()` — Exposed transaction のラッパー。エラーを `Res` 型で返す。

```kotlin
DbPersister.execTransaction {
    UserTable.select { UserTable.id eq userId }
        .singleOrNull()
        ?: throw NoSingleRecordErr("User not found")
}
```

エラー型: `NoSingleRecordErr`、`MultipleRecordErr`、`BackendErr`

## 未実装機能

- `DbAuthenticatedPersisterSession.createTask()` — タスク作成の永続化
- タスク状態管理（pending / running / completed）
- タスク実行結果の保存
