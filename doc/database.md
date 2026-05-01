# Database Documentation

このドキュメントは、Kerutaのデータベースアーキテクチャとスキーマを説明します。

## データベースアーキテクチャ

### 三層永続化抽象化

```
PersisterSession (インターフェース)
  └── verify(authRequestMsg): Res<AuthenticatedPersisterSession>

AuthenticatedPersisterSession (インターフェース)
  ├── createTask(task): Res<Unit>
  └── getProviders(): Res<List<PersistedProvider>>

DbPersisterSession (KTSE実装)
  └── DbAuthenticatedPersisterSession (KTSE認証済み実装)
      ├── タスク作成（**TODO: 未実装**）
      └── プロバイダー取得（データベースから取得）
```

**設計意図:**
- 永続化メカニズムを抽象化（将来的に他のストレージへ切り替え可能）
- 認証状態で操作を分離
- 型安全なデータベースアクセス

## データベーススキーマ

### テーブル一覧

#### provider
外部認証プロバイダーの情報を保存

| カラム名 | 型 | 説明 |
|---------|-----|------|
| id | BIGINT | プライマリーキー |
| user_id | BIGINT | ユーザーID（外部キー） |
| issuer | VARCHAR | IdP発行者URL |
| audience | VARCHAR | トークンオーディエンス |
| name | VARCHAR | プロバイダー名 |
| create_at | TIMESTAMP | 作成日時 |

**制約:**
- UNIQUE(user_id, issuer, audience)
- FOREIGN KEY(user_id) REFERENCES user(id)

#### user
システムユーザーの最小限情報

| カラム名 | 型 | 説明 |
|---------|-----|------|
| id | BIGINT | プライマリーキー |
| create_at | TIMESTAMP | 作成日時 |

#### user_idp
ユーザーとIdPの関係を管理

| カラム名 | 型 | 説明 |
|---------|-----|------|
| id | BIGINT | プライマリーキー |
| user_id | BIGINT | ユーザーID（外部キー） |
| subject | VARCHAR | IdPサブジェクト |
| issuer | VARCHAR | IdP発行者URL |
| create_at | TIMESTAMP | 作成日時 |

**制約:**
- UNIQUE(subject, issuer)
- FOREIGN KEY(user_id) REFERENCES user(id)

#### queue
タスクキューの情報

| カラム名 | 型 | 説明 |
|---------|-----|------|
| id | BIGINT | プライマリーキー |
| provider_id | BIGINT | プロバイダーID（外部キー） |
| name | VARCHAR | キュー名 |
| create_at | TIMESTAMP | 作成日時 |

**制約:**
- FOREIGN KEY(provider_id) REFERENCES provider(id)

#### queue_user
キューとユーザーの多対多関係

| カラム名 | 型 | 説明 |
|---------|-----|------|
| id | BIGINT | プライマリーキー |
| queue_id | BIGINT | キューID（外部キー） |
| user_id | BIGINT | ユーザーID（外部キー） |
| create_at | TIMESTAMP | 作成日時 |

**制約:**
- UNIQUE(queue_id, user_id)
- FOREIGN KEY(queue_id) REFERENCES queue(id)
- FOREIGN KEY(user_id) REFERENCES user(id)

#### task
タスク情報

| カラム名 | 型 | 説明 |
|---------|-----|------|
| id | BIGINT | プライマリーキー |
| user_id | BIGINT | ユーザーID（外部キー） |
| queue_id | BIGINT | キューID（外部キー） |
| name | VARCHAR | タスク名 |
| create_at | TIMESTAMP | 作成日時 |

**制約:**
- FOREIGN KEY(user_id) REFERENCES user(id)
- FOREIGN KEY(queue_id) REFERENCES queue(id)

### ER図

```
user (1) -----< (N) user_idp
  |
  |
  +------< (N) provider
  |              |
  |              |
  +------< (N) queue_user >----- (N) queue
  |                                    |
  |                                    |
  +------< (N) task >---------------< (N)
```

## データベースアクセス

### DbPersister

データベースアクセスの中心クラス。

**主要メソッド:**
- `execTransaction<T>()` - Exposed transactionのラッパー
- エラーハンドリングを統合
- Res型でエラーを返す

**実装場所:** `ktse/src/main/kotlin/net/kigawa/keruta/ktse/database/DbPersister.kt`

### DbPersisterDSL

型安全なクエリビルダー。

**使用例:**
```kotlin
DbPersister.execTransaction {
    val user = UserTable.select { UserTable.id eq userId }
        .singleOrNull()
        ?: throw NoSingleRecordErr("User not found")

    // ...
}
```

## マイグレーション

### Flyway

データベーススキーマのバージョン管理にFlywayを使用。

**マイグレーションファイル:**
- `V001__create_user_table.sql`
- `V002__create_user_idp_table.sql`
- `V003__create_provider_table.sql`
- `V004__create_queue_table.sql`
- `V005__create_queue_user_table.sql`
- `V006__create_task_table.sql`

**場所:** `ktse/src/main/resources/db/migration/`

### マイグレーション実行

アプリケーション起動時に自動実行：

```kotlin
val flyway = Flyway.configure()
    .dataSource(jdbcUrl, username, password)
    .load()

flyway.migrate()
```

## 接続プール

### HikariCP設定

```kotlin
HikariConfig().apply {
    jdbcUrl = System.getenv("DB_JDBC_URL") ?: "jdbc:mysql://localhost:3306/keruta"
    username = System.getenv("DB_USERNAME") ?: "keruta"
    password = System.getenv("DB_PASSWORD") ?: "keruta"
    maximumPoolSize = 10
}
```

**環境変数:**
- `DB_JDBC_URL` - JDBC接続URL
- `DB_USERNAME` - データベースユーザー名
- `DB_PASSWORD` - データベースパスワード

## トランザクション管理

### Exposed ORM

Kotlin製のORMライブラリ。

**特徴:**
- 型安全なクエリDSL
- コルーチンサポート
- トランザクション管理

**トランザクション例:**
```kotlin
transaction {
    val user = UserTable.insert {
        it[createAt] = Clock.System.now()
    } get UserTable.id

    UserIdpTable.insert {
        it[userId] = user
        it[subject] = "user-123"
        it[issuer] = "https://example.auth0.com/"
        it[createAt] = Clock.System.now()
    }
}
```

## データベースエラー

### エラー型

- `NoSingleRecordErr` - 期待した単一レコードが見つからない
- `MultipleRecordErr` - 単一レコードを期待したが複数存在
- `BackendErr` - 一般的なデータベースエラー

### エラーハンドリング例

```kotlin
when (val result = DbPersister.execTransaction { /* ... */ }) {
    is Res.Ok -> {
        // 成功処理
    }
    is Res.Err -> {
        when (result.err) {
            is NoSingleRecordErr -> // レコード未発見
            is MultipleRecordErr -> // 重複レコード
            else -> // その他のエラー
        }
    }
}
```

## ベストプラクティス

1. **トランザクションスコープを小さく**: 必要最小限の操作のみトランザクションに含める
2. **インデックス活用**: 頻繁に検索されるカラムにインデックスを追加
3. **外部キー制約**: データ整合性を保証
4. **タイムスタンプ記録**: 全テーブルに`create_at`を含める
5. **NULL禁止**: 可能な限りNOT NULL制約を使用

## 未実装機能

1. **タスク作成の永続化**: `DbAuthenticatedPersisterSession.createTask()`
2. **タスク状態管理**: タスクの進行状態（pending、running、completed等）
3. **タスク結果保存**: タスク実行結果の永続化
4. **監査ログ**: データ変更履歴の記録
5. **ソフトデリート**: 論理削除機能