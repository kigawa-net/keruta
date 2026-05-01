# Architecture

## Core Architecture Patterns

### Entrypointパターン

プロジェクトの中核となる設計パターン。型安全な双方向メッセージルーティングを実現。

```kotlin
interface Entrypoint<C, A> {
    suspend fun exec(arg: A, ctx: C): EntrypointDeferred<Res<Unit, KtcpErr>>
}

// 例: タスク作成エントリーポイント
ServerTaskCreateEntrypoint<C>
  ├── ReceiveTaskCreateEntrypoint : ServerTaskCreateEntrypoint<ServerCtx>
  └── SendTaskCreateEntrypoint   : ServerTaskCreateEntrypoint<ClientCtx>
```

- 各エントリーポイントは `Arg` 型（メッセージを含む）を受け取る
- コンテキスト型でサーバー/クライアントを区別（`ServerCtx` / `ClientCtx`）
- コンテキストからセッション、シリアライザ、サーバー状態にアクセス可能

### Res\<T, E\> パターン

例外を使わない関数型エラーハンドリング。

```kotlin
sealed interface Res<out T, out E : Throwable>
  ├── Ok<T, E>
  └── Err<T, E>
```

主要ユーティリティ: `convertOk`、`convertErr`、`flat`、`flatConvertOk`、`whenOkErr`、`whenErrOk`

## Message Flow and Routing

### メッセージ受信フロー（クライアント → サーバー）

1. `WebsocketModule.websocketModule()` で WebSocketフレーム受信
2. `ReceiveUnknownArg.fromFrame()` でタイプ判定
3. `KtcpServer.ktcpServerEntrypoints` 経由でルーティング
4. 専用エントリーポイントを実行（例: `ReceiveTaskCreateEntrypoint`）
5. 必要に応じ `KtcpServer.clientEntrypoints` 経由でレスポンス送信

### メッセージ送信フロー（サーバー → クライアント）

1. エントリーポイントが `SendXxxArg` を作成
2. `ctx.server.clientEntrypoints.xxx` でエントリーポイント取得
3. `ctx.serializer.serialize(msg)` でシリアライズ
4. `connection.send(serializedMsg)` で送信

### タスク作成フロー

1. クライアントが `SendTaskCreateEntrypoint` でメッセージ送信
2. サーバーが `ReceiveTaskCreateEntrypoint` で受信
3. セッション認証チェック（未認証 → `UnauthenticatedErr`）
4. `TaskToCreate.from()` でメッセージ→タスクデータ変換
5. `AuthenticatedPersisterSession.createTask()` → DB永続化

## シリアライゼーション

- インターフェース: `KerutaSerializer`
- 実装: `JsonKerutaSerializer`（kotlinx.serialization）
- `ClientMsg` / `ServerMsg` のシールドインターフェースでポリモーフィックシリアライズ
- 型判別子: `ClientMsgTypeSerializer`、`ServerMsgTypeSerializer`

## エラーハンドリング階層

```
KtcpErr（プロトコルエラーの基底）
  ├── KtcpServerErr
  │   ├── UnauthenticatedErr
  │   ├── VerifyErr
  │   │   ├── VerifyFailErr
  │   │   └── VerifyUnsupportedKeyErr
  │   ├── DeserializeErr
  │   └── ResponseErr
  └── モデルレベルエラー（ktcp-sdk）
      ├── EntrypointNotFoundErr
      ├── IllegalFormatDeserializeErr
      └── InvalidTypeDeserializeErr

KicpErr（kicp固有）
  ├── JwksFetchErr
  ├── TokenVerificationErr
  ├── RegisterTokenNotFoundErr
  ├── RegisterTokenExpiredErr
  └── PeerVerificationErr

BackendErr（KTSE固有）
  ├── NoSingleRecordErr
  └── MultipleRecordErr
```

## セッション管理

- タイムアウト: 1分間
- エラー許容: 30分の時間窓で3回まで
- 認証状態: `MutableStateFlow<AuthenticatedSession?>` で管理
- ライフサイクル: `WebsocketModule` で開始、`coroutineScope` 内で実行

## Gradle buildSrc プラグイン

`buildSrc/src/main/kotlin/` に集約。

| プラグイン | 用途 |
|---|---|
| `root.gradle.kts` | グループ/バージョンを `VERSION` 環境変数から設定 |
| `kmp.gradle.kts` | Kotlin Multiplatform基本設定（JVM、JS、WasmJS） |
| `serialize.gradle.kts` | kotlinx.serialization |
| `jvm.gradle.kts` | JVMのみモジュール向け基本設定 |
| `ktcp-base.gradle.kts` | KTCP共通依存（kodel:api） |
| `ktcp-model.gradle.kts` | KTCP model（KMP） |
| `ktcp-model-server.gradle.kts` | KTCP model + Auth0 JWT |
| `ktcp-server.gradle.kts` | KTCP server（Ktor WebSocket + コルーチン） |
| `ktcp-client.gradle.kts` | KTCP client |
| `ktor-server.gradle.kts` | Ktor server基本 |
| `ktor-server-websocket.gradle.kts` | Ktor WebSocket追加 |
| `compose-mobile-app.gradle.kts` | モバイルアプリ（Compose） |
| `ktcl-web.gradle.kts` | Web client |
