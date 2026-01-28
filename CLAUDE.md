# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

* ユーザーには日本語で応答する
* 大きなファイルは細分化する

## Programs

* 純粋関数を使う
* SOLID原則に従う
* Kotlin Multiplatform対応（JVM、JS両対応）

## Development Commands

### Quick Start
```bash
# Build all modules
./gradlew build

# Run Keruta Task Server (KTSE)
./gradlew :ktse:run

# Run Keruta Task Client Web (KTCL-Web)
./gradlew :ktcl-web:run

# Clean and rebuild everything
./gradlew clean build
```

### Building and Running
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :ktse:build
./gradlew :ktcp:server:build
./gradlew :kodel:api:build

# Run Keruta Task Server
./gradlew :ktse:run

# Run Keruta Task Client Web
./gradlew :ktcl-web:run
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :ktcp:server:test
./gradlew :ktse:test

# Run tests with detailed output
./gradlew test --continue
```

### Code Quality
```bash
# Format and check all code (run before committing)
./gradlew ktlintFormat && ./gradlew ktlintCheck

# Check code style
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat
```

## Architecture Overview

Kerutaは、タスク実行とWebSocket通信を中心としたKotlin Multiplatformプロジェクトです。

### Core Components
1. **KTCP (Keruta TCP Protocol)** - WebSocketベースの通信プロトコル
2. **KTSE (Keruta Task Server)** - Ktorベースのタスクサーバー（ZooKeeper統合）
3. **KTCL-Web (Keruta Task Client Web)** - Ktorベースのクライアントウェブアプリケーション
4. **Kodel** - 共通ライブラリ（API、コア、コルーチン）
5. **Keruta SDK** - クライアントSDK
6. **/todo.md** - todolist

### Multi-Module Structure

#### KTCP (Keruta TCP Protocol)
WebSocketベースの通信プロトコル実装。Kotlin Multiplatform対応（commonMain、jsMain、jvmMain）。

- **ktcp:model** - メッセージモデル、シリアライザ
  - `net.kigawa.keruta.ktcp.model` - KtcpMsg、KtcpConnection
  - `net.kigawa.keruta.ktcp.model.serialize` - KerutaSerializer（JSON）
  - `net.kigawa.keruta.ktcp.model.task` - ServerTaskCreateMsg、ServerTaskCreateArg

- **ktcp:client** - クライアント実装
  - `net.kigawa.keruta.ktcp.client` - KtcpClient、ClientCtx、ClientConnection
  - `net.kigawa.keruta.ktcp.client.task` - SendTaskCreateEntrypoint

- **ktcp:server** - サーバー実装
  - `net.kigawa.keruta.ktcp.server` - KtcpServer、ServerCtx、KtcpSession
  - `net.kigawa.keruta.ktcp.server.persist` - PersisterSession、AuthenticatedPersisterSession、TaskToCreate
  - `net.kigawa.keruta.ktcp.server.task` - ReceiveTaskCreateEntrypoint
  - **注意**: 認証機能（ServerAuthenticateEntrypoint）は未実装（スタブ実装）

#### KTSE (Keruta Task Server)
KtorベースのWebSocketサーバー。データベースでタスクを永続化。

- **パッケージ構成**:
  - `net.kigawa.keruta.ktse` - KerutaTaskServer（メインアプリケーション）
  - `net.kigawa.keruta.ktse.websocket` - WebSocketModule、WebsocketConnection
  - `net.kigawa.keruta.ktse.database` - データベース永続化
    - `DbPersister` - データベースアクセス抽象化
    - `DbPersisterSession` - データベース永続化セッション
    - `DbAuthenticatedPersisterSession` - 認証済みデータベースセッション
    - `DbPersisterDSL` - 型安全なクエリビルダー
  - `net.kigawa.keruta.ktse.zookeeper` - ZooKeeper統合（**現在ほぼ未使用**）
    - `ZkPersister` - ZooKeeperクライアント初期化のみ
    - `ServerWatcher` - ZooKeeper接続監視
  - `net.kigawa.keruta.ktse.auth` - 認証検証
    - `UserVerifier` - ユーザートークン検証
    - `ProviderVerifier` - プロバイダートークン検証
    - `Auth0JwtVerifier` - JWT検証とJWKキャッシング
  - `net.kigawa.keruta.ktse.task` - ReceiveTaskCreateArg
  - `net.kigawa.keruta.ktse.err` - BackendErr（エラーハンドリング）

- **主な機能**:
  - WebSocket通信（Ktor WebSockets）
  - データベースによるタスク永続化（Exposed ORM、Flyway migrations）
  - 二重トークン認証（ユーザー + プロバイダー）
  - セッション管理（PersisterSession抽象化）
  - エラーハンドリング（BackendErr）

#### KTCL-Web (Keruta Task Client Web)
KtorベースのWebクライアントアプリケーション。JWT認証とWebSocket通信をサポート。

- **パッケージ構成**:
  - `net.kigawa.keruta.ktcl.web` - KerutaTaskClientWeb（メインアプリケーション）
  - `net.kigawa.keruta.ktcl.web.module` - JwtModule、WebsocketModule

- **主な機能**:
  - JWT認証
  - CORS対応
  - WebSocket通信
  - セッション管理（UserSession）
  - KtcpClientとの統合

#### Kodel (共通ライブラリ)
プロジェクト共通のユーティリティライブラリ。

- **kodel:api** - エントリーポイント、エラー処理（Res型）、ログ
  - `net.kigawa.kodel.api.entrypoint` - EntrypointDeferred
  - `net.kigawa.kodel.api.err` - Res（Result型）
  - `net.kigawa.kodel.api.log` - Kogger（ロガー）、LoggerFactory

- **kodel:core** - コア機能

- **kodel:coroutine** - コルーチンユーティリティ

#### Keruta SDK
クライアントSDKプロジェクト（別Gradleプロジェクト）。

### 詳細ドキュメント

より詳細な情報は、以下のドキュメントを参照してください：

- **[Architecture Documentation](doc/architecture.md)** - アーキテクチャパターン、メッセージフロー、エラーハンドリング
- **[Authentication Documentation](doc/authentication.md)** - 二重トークン認証、JWT検証、セキュリティ設定
- **[Database Documentation](doc/database.md)** - データベーススキーマ、永続化アーキテクチャ、マイグレーション
- **[Development Documentation](doc/development.md)** - 開発環境セットアップ、トラブルシューティング、デバッグ

### Key Domain Models

- **KtcpMsg**: KTCP通信メッセージの基底インターフェース
- **ServerTaskCreateMsg**: タスク作成メッセージ（タスク名を含む）
- **PersisterSession**: タスク永続化セッションの抽象化
- **AuthenticatedPersisterSession**: 認証済みセッション（タスク作成、プロバイダー取得）
- **Res<T, E>**: Result型パターン（例外を使わないエラーハンドリング）

## Task Creation Flow

1. クライアントが`SendTaskCreateEntrypoint`を使ってタスク作成メッセージを送信
2. サーバーが`ReceiveTaskCreateEntrypoint`でメッセージを受信
3. セッション認証チェック（未認証の場合はUnauthenticatedErr）
4. `AuthenticatedPersisterSession.createTask()`を呼び出し
5. データベースにタスクを永続化（**注意**: 実装未完了）

詳細なメッセージフローとルーティングは[Architecture Documentation](doc/architecture.md)を参照。

## Important Implementation Details

### データベース永続化

**三層アーキテクチャ:**

```
PersisterSession (インターフェース)
  └── verify(authRequestMsg): Res<AuthenticatedPersisterSession>

AuthenticatedPersisterSession (インターフェース)
  └── createTask(task): Res<Unit>
  └── getProviders(): Res<List<PersistedProvider>>

DbPersisterSession (KTSE実装)
  └── DbAuthenticatedPersisterSession (KTSE認証済み実装)
      ├── タスク作成（**TODO: 未実装**）
      └── プロバイダー取得（データベースから取得）
```

**データベーススキーマ:**
- `provider` - 外部認証プロバイダー（issuer、audience、name）
- `user` - システムユーザー（最小限の情報）
- `user_idp` - ユーザー-IdP関係（subject、issuer紐付け）
- `queue` - タスクキュー（プロバイダー参照）
- `queue_user` - 多対多のキューメンバーシップ
- `task` - タスク（ユーザー/キュー参照）

**データベースアクセス:**
- `DbPersister.execTransaction()` - Exposed transactionラッパー
- `DbPersisterDSL` - 型安全なクエリビルダー
- Flyway migrations（V001-V006スクリプト）
- HikariCP接続プール（最大10接続）
- MySQL対応

**環境変数:**
- `DB_JDBC_URL` - JDBC接続URL
- `DB_USERNAME` - データベースユーザー名
- `DB_PASSWORD` - データベースパスワード

### ZooKeeper統合（現在ほぼ未使用）
- `ZkPersister`が初期化されるが、実際のタスク永続化には使用されていない
- `ServerWatcher`でZooKeeper接続状態を監視
- 将来的な分散タスク永続化のためのプレースホルダー実装
- タスクの永続化は現在データベースで実行

### シリアライゼーション
- `KerutaSerializer`インターフェースで抽象化
- `JsonKerutaSerializer`でJSON形式のシリアライゼーション実装
- `kotlinx.serialization`を使用
- `MsgSerializer`から`KerutaSerializer`にリネーム

### エラーハンドリング
- `BackendErr`クラスでバックエンドエラーを表現
- `KtcpServerErr`を継承し、`ServerErrCode.BACKEND`を使用
- `Res<T, E>`型でResult型パターンを実装（`Res.Ok`、`Res.Err`）

### エラーハンドリング階層

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

### 未実装機能（TODO）

以下の機能は実装が未完了です：

1. **キュー作成**: `ReceiveQueueCreateEntrypoint`（未実装）
2. **タスク作成の永続化**: `DbAuthenticatedPersisterSession.createTask()`（未実装）
3. **セッションタイムアウトロジック**: `KtcpSession.startSession()`でコメントアウト
4. **ZooKeeper活用**: 初期化済みだが、タスク永続化には未使用

### 認証フロー（二重トークン検証）

**KTSE（サーバー）での認証:**

1. **ユーザートークン検証:**
   - ユーザーのIdP（例: Auth0）が発行したJWTを受信
   - `Auth0JwtVerifier`でトークンをデコード・検証
   - JWK（JSON Web Key）をキャッシュ（LRUキャッシュ、最大8発行者）
   - OIDC Discovery経由でjwks_urlを自動取得
   - サブジェクト（subject）を抽出し、ユーザーを特定
   - `UserVerifier`: 初回認証時にユーザー作成、以降は既存ユーザーを再利用

2. **プロバイダートークン検証:**
   - プロバイダーのIdPが発行したJWTを検証
   - ユーザーの権限スコープに対してプロバイダートークンを検証
   - `ProviderVerifier`: プロバイダー情報をデータベースから取得・照合

3. **セッション確立:**
   - 両トークンが検証されると、`AuthenticatedPersisterSession`を作成
   - セッション状態を`MutableStateFlow`に保存
   - `KtcpSession`でセッションライフサイクルを管理

**セキュリティ設定:**
- **RSA256アルゴリズム**: JWT署名検証に使用
- **タイムアウト**: 1分間タイムアウト、30分間で3エラーまで許容
- **開発環境**: CORS設定は開発用（anyHost()使用）
- **本番環境**: 本番用CORS設定への変更を推奨

### ZooKeeper設定（KTSE）
ZooKeeper接続は環境変数で設定：
- `KTSE_ZK_HOST` - ZooKeeperホスト（デフォルト: localhost:2181）

### JWT設定（KTCL-Web）
JWT認証は環境変数で設定：
- JWT関連の設定は`Config.load()`で読み込み

## Development Environment

### Local Setup (KTSE)
```bash
# ZooKeeperを起動（Docker使用の場合）
docker run -d --name zookeeper -p 2181:2181 zookeeper

# KTSEを起動
./gradlew :ktse:run
```

### Local Setup (KTCL-Web)
```bash
# KTCL-Webを起動
./gradlew :ktcl-web:run
```

## Code Style and Quality

### Kotlin Style
- ktlintでコードフォーマットとスタイルチェック
- `.editorconfig`に設定
- Kotlin Multiplatform対応（commonMain、jsMain、jvmMain）

### Testing Strategy
- JUnit 5フレームワーク
- Kotlin testフレームワーク
- テストは各モジュールの`src/test/kotlin`に配置

## Deployment

### Container Deployment
- Dockerイメージはそれぞれのモジュールから構築可能
- 環境変数で設定可能
- Harbor registry: `harbor.kigawa.net/library/keruta`

## Project Structure Notes

- **Kotlin Multiplatform**: JVMとJSの両方に対応
- **Gradleマルチモジュール**: 機能ごとにモジュール分割
- **共通ライブラリ（Kodel）**: プロジェクト共通のユーティリティ
- **プロトコル定義（KTCP）**: 通信プロトコルを独立したモジュールとして定義
- **サーバー（KTSE）**: Ktorベース、ZooKeeper統合
- **クライアント（KTCL-Web）**: Ktorベース、JWT認証

## Gradle Module Structure

```
keruta/
├── kodel/                  # 共通ライブラリ
│   ├── api/               # エントリーポイント、エラー処理、ログ
│   ├── core/              # コア機能
│   └── coroutine/         # コルーチンユーティリティ
├── ktcp/                  # Keruta TCPプロトコル
│   ├── model/             # メッセージモデル、シリアライザ
│   ├── client/            # クライアント実装
│   └── server/            # サーバー実装（認証未実装）
├── ktse/                  # Keruta Task Server (Ktor + ZooKeeper)
├── ktcl-web/              # Keruta Task Client Web (Ktor + JWT)
└── keruta-sdk/            # クライアントSDK（別Gradleプロジェクト）
```

## Common Issues and Solutions

### Build Issues
- **Gradle build cache issues**: `./gradlew clean build`を実行
- **ktlint failures**: `./gradlew ktlintFormat`を実行してからビルド

### Runtime Issues
- **ZooKeeper connection issues**: ZooKeeperが起動していることを確認
- **WebSocket connection failures**: CORSとWebSocket設定を確認
- **Authentication errors (KTCP)**: 認証機能が未実装のため、本番環境では使用しないこと
- **JWT token issues (KTCL-Web)**: JWT設定を環境変数で確認

### Development Workflow
1. コードフォーマット: `./gradlew ktlintFormat`
2. テスト実行: `./gradlew test`
3. ビルド: `./gradlew build`
4. サーバー起動: `./gradlew :ktse:run`


## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.
