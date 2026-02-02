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

# Run Keruta K8s Client (KTCL-K8s)
./gradlew :ktcl-k8s:run

# Run Keruta Front (React + Vite)
cd ktcl-front && npm install && npm run dev

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

# Run Keruta K8s Client
./gradlew :ktcl-k8s:run

# Run Keruta Claude Code Client
./gradlew :ktcl-claudecode:run

# Run Keruta Front (React + Vite)
cd ktcl-front && npm run dev
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
2. **KTSE (Keruta Task Server)** - Ktorベースのタスクサーバー（データベース統合）
3. **KTCL-Front** - React + TypeScript + Vite フロントエンドWebアプリケーション
4. **KTCL-Front-Mobile** - Kotlin Multiplatform Mobile（iOS/Android）タスククライアント
5. **KTCL-K8s** - Kubernetes Job実行クライアント（タスクをK8s Jobとして実行）
6. **KTCL-ClaudeCode** - Claude Code統合クライアント
7. **KTCL-Web** - Ktorベースのクライアントウェブアプリケーション（レガシー）
8. **Kodel** - 共通ライブラリ（API、コア、コルーチン）
9. **Keruta SDK** - クライアントSDK（別リポジトリ）

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

#### KTCL-Front (Keruta Task Client Frontend)
React + TypeScript + ViteベースのフロントエンドWebアプリケーション。Keycloak OIDC認証、WebSocket通信をサポート。

- **技術スタック**:
  - React + TypeScript + Vite
  - Keycloak.js（OIDC認証）
  - WebSocket（KTCP通信）

- **ビルド環境変数**:
  - `VITE_WEBSOCKET_URL` - WebSocketエンドポイント
  - `VITE_KEYCLOAK_URL` - Keycloak URL
  - `VITE_KEYCLOAK_REALM` - Keycloakレルム
  - `VITE_KEYCLOAK_CLIENT_ID` - KeycloakクライアントID

#### KTCL-Front-Mobile (Keruta Task Client Mobile)
Kotlin Multiplatform MobileベースのiOS/Androidタスククライアント。KTCL-FrontをモバイルネイティブアプリとしてKotlinに移植。

- **技術スタック**:
  - Kotlin Multiplatform Mobile 2.3.0
  - Compose Multiplatform 1.10.0
  - Ktor Client（WebSocket通信）
  - AppAuth（OIDC認証）

- **主な機能**:
  - WebSocket双方向通信（Android: OkHttp、iOS: Darwin）
  - 二重トークン認証（userToken + serverToken）
  - SecureStorage（Android: EncryptedSharedPreferences、iOS: NSUserDefaults）
  - StateFlowベースのRepository パターン
  - TaskReceiver（メッセージ受信・処理）

- **既知の課題**:
  - iOSビルド: Compose Multiplatform 1.10.0のandroidx依存関係がiOS版未公開
  - iOS OIDC認証: AppAuth for iOS未実装（スタブ実装）
  - UI実装: 画面・Navigation未実装

- **詳細**: [ktcl-front-mobile/README.md](ktcl-front-mobile/README.md)を参照

#### KTCL-K8s (Keruta Kubernetes Client)
KTCPプロトコル経由でタスクを受信し、Kubernetes Jobとして実行するクライアント。

- **パッケージ構成**:
  - `net.kigawa.keruta.ktcl.k8s` - KerutaK8sClient（メインアプリケーション）
  - `net.kigawa.keruta.ktcl.k8s.auth` - AuthManager（KTCP認証）
  - `net.kigawa.keruta.ktcl.k8s.k8s` - K8sJobExecutor、K8sJobWatcher、JobTemplateLoader
  - `net.kigawa.keruta.ktcl.k8s.task` - TaskExecutor、TaskReceiver
  - `net.kigawa.keruta.ktcl.k8s.web` - Webモード（設定管理UI）

- **主な機能**:
  - KTCPプロトコル統合
  - 二重トークン認証（ユーザー + プロバイダー）
  - Kubernetes Job実行・監視
  - YAML定義ベースのJob設定
  - Webモード（Keycloak OIDC + 設定管理API）

- **起動モード**:
  - **CLIモード**（デフォルト）: 環境変数で設定、自動タスク実行
  - **Webモード**: `KTCL_K8S_WEB_MODE=true`でWeb UI有効化

#### KTCL-ClaudeCode (Keruta Claude Code Client)
Claude Code統合クライアント（開発中）。

#### KTCL-Web (Keruta Task Client Web - レガシー)
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

### 認証システム
- **二重トークン検証**: ユーザートークン + プロバイダートークン
- **JWT検証**: Auth0JwtVerifier、JWKキャッシング、OIDC Discovery
- **セッション管理**: MutableStateFlowでステート管理

詳細は[Authentication Documentation](doc/authentication.md)を参照。

### データベース
- **三層永続化抽象化**: PersisterSession → AuthenticatedPersisterSession → DbAuthenticatedPersisterSession
- **主要テーブル**: provider、user、user_idp、queue、queue_user、task
- **技術スタック**: Exposed ORM、Flyway、HikariCP、MySQL
- **環境変数**: `DB_JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD`

詳細は[Database Documentation](doc/database.md)を参照。

### シリアライゼーション
- `KerutaSerializer`インターフェース（抽象化）
- `JsonKerutaSerializer`実装（kotlinx.serialization使用）

### エラーハンドリング
- `Res<T, E>`型でResult型パターンを実装
- `KtcpErr`を基底とした階層的エラー型
- 詳細は[Architecture Documentation](doc/architecture.md)を参照

### ZooKeeper統合
- 現在ほぼ未使用（プレースホルダー実装）
- タスク永続化はデータベースで実行
- 環境変数: `KTSE_ZK_HOST`

### 未実装機能（TODO）
1. キュー作成エントリーポイント
2. タスク作成の永続化
3. セッションタイムアウトロジック
4. ZooKeeper活用

## Development Environment

### Quick Setup
```bash
# データベース起動（Docker）
docker-compose -f compose.test.yml up -d mysql

# 環境変数設定
export DB_JDBC_URL="jdbc:mysql://localhost:3306/keruta"
export DB_USERNAME="keruta"
export DB_PASSWORD="keruta"

# サーバー起動
./gradlew :ktse:run
```

詳細なセットアップ手順、トラブルシューティング、デバッグ方法は[Development Documentation](doc/development.md)を参照。

## Code Style and Quality

- **Kotlin Style**: ktlint（`.editorconfig`設定）
- **SOLID原則**: 純粋関数を優先
- **Testing**: JUnit 5 + Kotlin test
- **Development Workflow**: ktlintFormat → test → build

## Project Structure

```
keruta/
├── kodel/                  # 共通ライブラリ（API、コア、コルーチン）
├── ktcp/                  # Keruta TCPプロトコル（model、client、server）
├── ktse/                  # Keruta Task Server（Ktor + Database + WebSocket）
├── ktcl-front/            # Keruta Frontend（React + TypeScript + Vite）
├── ktcl-front-mobile/     # Keruta Mobile Frontend（Kotlin Multiplatform Mobile）
├── ktcl-k8s/              # Keruta K8s Client（Kubernetes Job実行）
├── ktcl-claudecode/       # Keruta Claude Code Client（開発中）
├── ktcl-web/              # Keruta Task Client Web（レガシー）
└── doc/                   # 詳細ドキュメント
```

## Deployment

### Dockerイメージのビルド

```bash
# KTSE（タスクサーバー）
docker build -f Dockerfile_ktse -t harbor.kigawa.net/library/ktse:latest .

# KTCL-K8s（Kubernetes Job実行クライアント）
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .

# KTCL-Front（Reactフロントエンド）
docker build -f Dockerfile_ktcl_front -t harbor.kigawa.net/private/ktcl-front:latest .
```

### GitHub Actions

- **developブランチ**: `dev.yml`が自動実行（ktse、ktcl-k8s、ktcl-frontをビルド・デプロイ）
- **Harbor Registry**: `harbor.kigawa.net/library/`（public）、`harbor.kigawa.net/private/`（private）
- **マニフェスト自動更新**: `kigawa-net/kigawa-net-k8s`リポジトリのマニフェストファイルを自動更新


## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
NEVER create files unless they're absolutely necessary for achieving your goal.
ALWAYS prefer editing an existing file to creating a new one.
NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User.
