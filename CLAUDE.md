# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

* ユーザーには日本語で応答する
* 大きなファイルは細分化する

## Programs

* 純粋関数を使う
* SOLID原則に従う
* Kotlin Multiplatform対応（JVM、JS両対応）

## Development Commands

```bash
./gradlew build                          # 全モジュールビルド
./gradlew :ktse:build                    # 個別モジュール
./gradlew :ktse:run                      # タスクサーバー起動
./gradlew :ktcl-k8s:run                  # K8sクライアント起動
cd ktcl-front && npm run dev             # フロントエンド起動
./gradlew test --tests "net.kigawa.keruta.ktse.ReceiveUnknownArgTest"  # 単一テスト
./gradlew test --continue                # 失敗しても続行
./gradlew ktlintFormat && ./gradlew ktlintCheck  # コミット前
docker-compose -f compose.test.yml up -d mysql   # DB起動
```

## Architecture Overview

マルチモジュール構成。依存性バージョンは `buildSrc/src/main/kotlin/Version.kt` で一元管理。

- **kodel** - 共通ライブラリ（Res型、EntrypointDeferred、Kogger）
- **ktcp-sdk** - WebSocketプロトコル（model/client/server、Kotlin Multiplatform対応）
- **ktse** - KtorタスクサーバーWS（Exposed/Flyway/MySQL、二重トークン認証）
- **ktcl-k8s** - KTCPでタスク受信しKubernetes Jobとして実行
- **ktcl-front** - React+TypeScript+Vite+Keycloak.js
- **ktcl-claudecode** - Claude Code統合（開発中）
- 詳細ドキュメント: `doc/` 配下参照

## Key Architectural Patterns

### Entrypoint Groupパターン（メッセージルーティング）
KTCPの中核。`KtcpClientEntrypoints<C>` が受信メッセージを14種類の型に分類してハンドラにルーティング。

```
受信テキスト → ReceiveClientUnknownArg.fromText() → tryTo*()で型判定
           → clientEntrypoints.access(unknownArg, ctx)?.execute()
```

### Res<T, E> パターン（エラーハンドリング）
例外を使わない型安全なエラーハンドリング。`Res.Ok<T>` / `Res.Err<E>` の sealed interface。

### 手動DIとFactoryパターン
DIフレームワーク不使用。Kotlin Multiplatform対応のため手動Factoryで依存性を構成。
- `TaskExecutorFactory` - K8s実行コンポーネントを組み立て
- `ClientEntrypointsFactory` - 14個のメッセージハンドラを初期化
- `K8sClientFactory` (object) - K8sクライアントのシングルトン生成

## KTCL-K8s パッケージ構成

- `connection` - ConnectionContext（接続コンテキスト）、ReceiveClientUnknownArg（型判定）
- `entrypoint` - ClientEntrypointsFactory
- `task` - TaskReceiver（受信ループ）、TaskExecutor、TaskExecutorFactory
- `k8s` - K8sJobExecutor、K8sJobWatcher、K8sClientFactory
- `auth` - AuthManager（KTCP認証）
- `web` - Webモード（`KTCL_K8S_WEB_MODE=true` で有効化）

接続フロー: `connectAndCreateSession()` → `authenticate()` → `requestInitialTaskList()` → TaskReceiverループ

## Testing

JUnit 5 + Kotlin Test + MockK。テスト関数名はバッククオート形式。

## Deployment

```bash
docker build -f Dockerfile_ktse -t harbor.kigawa.net/library/ktse:latest .
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .
docker build -f Dockerfile_ktcl_front -t harbor.kigawa.net/private/ktcl-front:latest .
```

developブランチへのpushで `dev.yml` が自動ビルド・デプロイ（Harbor Registry → kigawa-net-k8s マニフェスト更新）

## important-instruction-reminders
Do what has been asked; nothing more, nothing less.
