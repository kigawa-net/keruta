# Architecture Documentation

このドキュメントは、Kerutaプロジェクトのアーキテクチャと実装パターンを説明します。

## Core Architecture Patterns

### Entrypointパターン

プロジェクトの中核となる設計パターン。型安全な双方向メッセージルーティングを実現。

**構造:**
```kotlin
interface Entrypoint<C, A> {
    suspend fun exec(arg: A, ctx: C): EntrypointDeferred<Res<Unit, KtcpErr>>
}

// 例: タスク作成エントリーポイント
ServerTaskCreateEntrypoint<C>
  ├── ReceiveTaskCreateEntrypoint : ServerTaskCreateEntrypoint<ServerCtx>
  └── SendTaskCreateEntrypoint : ServerTaskCreateEntrypoint<ClientCtx>
```

**特徴:**
- 各エントリーポイントは`Arg`型（メッセージを含む）を受け取る
- コンテキスト型でサーバー/クライアントを区別（`ServerCtx` / `ClientCtx`）
- コンテキストからセッション、シリアライザ、サーバー状態にアクセス可能
- 非同期実行のため`EntrypointDeferred<Res<T, E>>`を返す

### Res型（Result型パターン）

例外を使わない関数型エラーハンドリング。全ての操作で使用。

```kotlin
sealed interface Res<out T, out E : Throwable>
  ├── Ok<T> : Res<T, Nothing>
  └── Err<E> : Res<Nothing, E>
```

**主要メソッド:**
- `convertOk`, `convertErr` - 型変換
- `flat`, `flatOk` - ネスト解消
- `whenOkErr`, `whenErrOk` - 条件分岐処理

## Message Flow and Routing

### メッセージ受信フロー（クライアント → サーバー）

1. `WebsocketModule.websocketModule()`でWebSocketフレームを受信
2. `ReceiveUnknownArg.fromFrame()`でフレームをデコードし、メッセージタイプを判定
3. `KtcpServer.ktcpServerEntrypoints`のエントリーポイントグループ経由でルーティング
4. メッセージタイプに応じた専用エントリーポイントを実行（例: `ReceiveTaskCreateEntrypoint`）
5. レスポンスが必要な場合、`KtcpServer.clientEntrypoints`経由でクライアントへ送信

### メッセージ送信フロー（サーバー → クライアント）

1. エントリーポイントが`SendXxxArg`（送信用引数）を作成
2. `ctx.server.clientEntrypoints.xxx`でクライアント向けエントリーポイントにアクセス
3. `SendXxxEntrypoint`をコンテキスト付きで呼び出し
4. `ctx.serializer.serialize(msg)`でメッセージをシリアライズ
5. `connection.send(serializedMsg)`でWebSocket経由で送信

### メッセージタイプ

**サーバーメッセージ（クライアントが受信）:**
- `GENERIC_ERROR`, `AUTH_REQUEST`, `AUTH_SUCCESS`, `TASK_CREATE`, `PROVIDER_LIST`, `QUEUE_CREATE`, `QUEUE_LIST`

**クライアントメッセージ（サーバーが受信）:**
- `GENERIC_ERROR`, `PROVIDER_LIST`

### タスク作成フロー

1. クライアントが`SendTaskCreateEntrypoint`を使ってタスク作成メッセージを送信
2. サーバーが`ReceiveTaskCreateEntrypoint`でメッセージを受信
3. セッション認証チェック（未認証の場合はUnauthenticatedErr）
4. `TaskToCreate.from()`でメッセージをタスクデータに変換
5. `AuthenticatedPersisterSession.createTask()`を呼び出し
6. データベースにタスクを永続化

## シリアライゼーション

### KerutaSerializer

- インターフェース: `KerutaSerializer`で抽象化
- 実装: `JsonKerutaSerializer`（kotlinx.serialization使用）
- 型システム: `ClientMsg`、`ServerMsg`のシールドインターフェースでポリモーフィックシリアライゼーション
- 型判別子: `ClientMsgTypeSerializer`、`ServerMsgTypeSerializer`
- エラーハンドリング: `SerializationException`と`IllegalArgumentException`をキャッチ

## エラーハンドリング階層

```
Throwable
  └── KtcpErr (プロトコルエラーの基底)
      ├── KtcpServerErr (ServerErrCodeで拡張)
      │   ├── UnauthenticatedErr - 未認証エラー
      │   ├── VerifyErr - 検証エラー
      │   │   ├── VerifyFailErr - 検証失敗
      │   │   └── VerifyUnsupportedKeyErr - 未サポートキー
      │   ├── DeserializeErr - デシリアライズエラー
      │   └── ResponseErr - レスポンスエラー
      │
      └── モデルレベルエラー（ktcp:model）
          ├── EntrypointNotFoundErr - エントリーポイント未発見
          ├── IllegalFormatDeserializeErr - 不正フォーマット
          └── InvalidTypeDeserializeErr - 無効な型

バックエンド固有エラー（KTSE）:
  └── BackendErr - ZooKeeper例外ラッパー
  └── データベースエラー: NoSingleRecordErr, MultipleRecordErr
```

## セッション管理

### KtcpSession

- **タイムアウト処理**: 1分間タイムアウト、30分間の時間窓で3エラーまで許容
- **認証状態**: `MutableStateFlow<AuthenticatedSession?>`で管理
- **エラー記録**: `counterInDuration`でエラー率を追跡
- **ライフサイクル**: `WebsocketModule`で開始、`coroutineScope`内で実行

### 認証状態遷移

1. 未認証セッション（`PersisterSession`）
2. 認証リクエスト受信
3. トークン検証（ユーザー + プロバイダー）
4. 認証済みセッション（`AuthenticatedPersisterSession`）
5. セッション状態を`StateFlow`に保存

## Gradle Build Architecture

**Build Plugins (buildSrc/src/main/kotlin/):**
- `root.gradle.kts` - グループ/バージョンをVERSION環境変数から設定
- `kmp.gradle.kts` - Kotlin Multiplatform基本設定（JVM、JS、WasmJS）
- `ktcp-model.gradle.kts` - Kodel API依存関係を追加
- `ktcp-server.gradle.kts` - Auth0 JWT、コルーチン、シリアライゼーションを追加
- `ktcp-client.gradle.kts` - シリアライゼーションのみ
- `ktse.gradle.kts` - Ktor WebSocket、ZooKeeper、Database（Exposed、Flyway、HikariCP、MySQL）
- `ktor-server-websocket.gradle.kts` - WebSocket依存関係

**ビルド戦略:**
- マルチモジュール構成で各機能を独立管理
- 共通設定をbuildSrcで一元化
- Kotlin Multiplatform対応（JVM、JS、WasmJS）