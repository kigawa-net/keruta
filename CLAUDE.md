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
KtorベースのWebSocketサーバー。ZooKeeperと統合し、タスクの永続化を実現。

- **パッケージ構成**:
  - `net.kigawa.keruta.ktse` - KerutaTaskServer（メインアプリケーション）
  - `net.kigawa.keruta.ktse.websocket` - WebSocketModule、WebsocketConnection
  - `net.kigawa.keruta.ktse.zookeeper` - ZooKeeper統合
    - `ZkPersister` - タスク永続化
    - `ZkPersisterSession` - 永続化セッション
    - `ZkAuthenticatedPersisterSession` - 認証済み永続化セッション
    - `ServerWatcher` - ZooKeeper接続監視
  - `net.kigawa.keruta.ktse.task` - ReceiveTaskCreateArg
  - `net.kigawa.keruta.ktse.err` - BackendErr（エラーハンドリング）

- **主な機能**:
  - WebSocket通信（Ktor WebSockets）
  - ZooKeeperによるタスク永続化
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

### Key Domain Models

- **KtcpMsg**: KTCP通信メッセージの基底インターフェース
- **ServerTaskCreateMsg**: タスク作成メッセージ（タスク名を含む）
- **TaskToCreate**: 永続化するタスクデータ
- **PersisterSession**: タスク永続化セッションの抽象化
- **AuthenticatedPersisterSession**: 認証済みセッション（タスク作成機能を提供）
- **KtcpConnection**: WebSocket接続の抽象化

## Task Creation Flow

### クライアントからサーバーへのタスク作成フロー
1. クライアントが`SendTaskCreateEntrypoint`を使ってタスク作成メッセージを送信
2. サーバーが`ReceiveTaskCreateEntrypoint`でメッセージを受信
3. セッション認証チェック（未認証の場合はUnauthenticatedErr）
4. `TaskToCreate.from()`でメッセージをタスクデータに変換
5. `AuthenticatedPersisterSession.createTask()`を呼び出し
6. `ZkAuthenticatedPersisterSession`がZooKeeperにタスクを永続化
7. `ZkPersister.createTask()`がZooKeeperノードを作成

## Important Implementation Details

### ZooKeeper統合
- タスクの永続化にApache ZooKeeperを使用
- `ZkPersister`がZooKeeperクライアントを管理
- `ServerWatcher`で接続状態を監視
- タスクは`KerutaSerializer`でシリアライズされてZooKeeperに保存

### シリアライゼーション
- `KerutaSerializer`インターフェースで抽象化
- `JsonKerutaSerializer`でJSON形式のシリアライゼーション実装
- `kotlinx.serialization`を使用
- `MsgSerializer`から`KerutaSerializer`にリネーム

### エラーハンドリング
- `BackendErr`クラスでバックエンドエラーを表現
- `KtcpServerErr`を継承し、`ServerErrCode.BACKEND`を使用
- `Res<T, E>`型でResult型パターンを実装（`Res.Ok`、`Res.Err`）

### Recent Architecture Changes
- **ZooKeeper統合**: タスク永続化機能を追加
- **PersisterSession抽象化**: セッション永続化の抽象化レイヤーを導入
- **BackendErr追加**: バックエンドエラーハンドリングクラスを追加
- **KerutaSerializerリネーム**: MsgSerializerからKerutaSerializerに名称変更
- **KtcpConnection追加**: 接続インターフェースを追加
- **タスク作成メッセージ**: ServerTaskCreateMsg、ServerTaskCreateArgを追加
- **タスク作成エントリーポイント**: ServerTaskCreateEntrypoint、ReceiveTaskCreateEntrypointを実装

### セキュリティモデル
**注意**: 現在、認証機能は未実装です。

- **KTCP Server**: ServerAuthenticateEntrypointが未実装（スタブ実装）
- **KTCL-Web**: JWT認証機能を実装済み
- **開発環境**: CORS設定は開発用（anyHost()使用）
- **本番環境**: 認証機能実装後に本番使用を推奨

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
