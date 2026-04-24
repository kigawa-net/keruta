# AGENTS.md

This file provides guidance for AI agents working with this codebase.
日本語で話す。

## 開発コマンド

### ビルド

```bash
./gradlew build                          # 全モジュールビルド
./gradlew :ktse:build                    # 個別モジュール
./gradlew :ktse:run                      # タスクサーバー起動
./gradlew :ktcl-k8s:run                  # K8sクライアント起動
cd ktcl-front && npm run dev             # フロントエンド起動
```

### テスト

```bash
./gradlew test                           # 全テスト実行
./gradlew test --tests "net.kigawa.keruta.ktse.ReceiveUnknownArgTest"  # 単一テスト
./gradlew test --tests "*ReceiveUnknownArgTest"  # クラス名指定
./gradlew test --continue                # 失敗しても続行
```

### リント・フォーマット

```bash
./gradlew ktlintFormat                   # 自動フォーマット
./gradlew ktlintCheck                    # リントチェック
```

### DB

```bash
docker-compose -f compose.test.yml up -d mysql   # テスト用MySQL起動
```

## コードスタイルガイドライン

### 基本設定

- **IDE**: EditorConfig対応 (`.editorconfig`)
- **インデント**: スペース4
- **最大行長**: 120文字
- **KtLintスタイル**: intellij_idea

### Kotlin Multiplatform

- JVMとJS両対応が前提
- `commonMain`に共通コードを配置
- プラットフォーム固有コードは `jvmMain`, `jsMain`, `iosMain` 等に配置

### 命名規則

| 種類 | 規則 | 例 |
|------|------|-----|
| クラス/インターフェース | PascalCase | `TaskExecutorFactory`, `KtcpErr` |
| 関数/変数 | camelCase | `create()`, `config` |
| 定数 | UPPER_SNAKE_CASE | `KOTLIN = "2.3.0"` |
| エラー型 | *Err | `KtcpErr`, `BackendErr` |
| テスト関数 | バッククォート | `testServerMsgTypeStringComparison()` |

### パッケージ構成

```
net.kigawa.keruta.{module}.{layer}.{feature}
```

レイヤー: `persist`, `websocket`, `auth`, `err`, `entrypoint`, `task`, `k8s`, etc.

### Imports

- 完全修飾名を使用（ワイルドカードインポートは無効化済み）
- グループ順: stdlib → external → project

### エラーハンドリング

- **推奨**: `Res<T, E>` パターン (kodel/api)
  ```kotlin
  fun doSomething(): Res<Output, SomeErr> = when(val result = action()) {
      is Res.Err -> result.convert()
      is Res.Ok -> Res.Ok(process(result.value))
  }
  ```
- 例外使用時は `Err` サフィックスの抽象基底クラスを使用
- 具体的なエラーは `Err` クラスのサブクラスとして定義

### DIパターン

- DIフレームワーク不使用（Kotlin Multiplatform対応）
- 手動Factoryクラスで依存性を注入
- Factoryクラス名は `{Component}Factory`

### アーキテクチャパターン

- **Res<T, E>**: 例外を使わない型安全なエラーハンドリング
- **Entrypoint**: 入力→出力の関数型インターフェース
- **Entrypoint Group**: メッセージルーティング（KTCPの中核）

### テスト

- JUnit 5 + Kotlin Test + MockK
- テストクラスは `{ClassName}Test`
- テスト関数はバッククォート形式で命名

### その他

- 純粋関数を使用
- SOLID原則に従う
- 大きなファイルは細分化
- KDocコメントで文書化（日本語）
- 日本語使う

## アーキテクチャ概要

### モジュール構成

マルチモジュール構成。依存性バージョンは `buildSrc/src/main/kotlin/Version.kt` で一元管理。

- **kodel** - 共通ライブラリ（Res型、EntrypointDeferred、Kogger）
- **ktcp** - WebSocketプロトコル（model/client/server、Kotlin Multiplatform対応）
- **ktse** - KtorタスクサーバーWS（Exposed/Flyway/MySQL、二重トークン認証）
- **ktcl-k8s** - KTCPでタスク受信しKubernetes Jobとして実行
- **ktcl-front** - React+TypeScript+Vite+Keycloak.js
- **ktcl-claudecode** - Claude Code統合
- 詳細ドキュメント: `doc/` 配下参照

### Entrypoint Groupパターン（メッセージルーティング）

KTCPの中核。`KtcpClientEntrypoints<C>` が受信メッセージを14種類の型に分類してハンドラにルーティング。

```
受信テキスト → ReceiveClientUnknownArg.fromText() → tryTo*()で型判定
            → clientEntrypoints.access(unknownArg, ctx)?.execute()
```

### KTCL-K8s パッケージ構成

- `connection` - ConnectionContext（接続コンテキスト）、ReceiveClientUnknownArg（型判定）
- `entrypoint` - ClientEntrypointsFactory
- `task` - TaskReceiver（受信ループ）、TaskExecutor、TaskExecutorFactory
- `k8s` - K8sJobExecutor、K8sJobWatcher、K8sClientFactory
- `auth` - AuthManager（KTCP認証）
- `web` - Webモード（`KTCL_K8S_WEB_MODE=true` で有効化）

接続フロー: `connectAndCreateSession()` → `authenticate()` → `requestInitialTaskList()` → TaskReceiverループ

## デプロイ

```bash
docker build -f Dockerfile_ktse -t harbor.kigawa.net/library/ktse:latest .
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .
docker build -f Dockerfile_ktcl_front -t harbor.kigawa.net/private/ktcl-front:latest .
```

developブランチへのpushで `dev.yml` が自動ビルド・デプロイ（Harbor Registry → kigawa-net-k8s マニフェスト更新）

## 依存バージョン

- Kotlin: 2.3.0
- Ktor: 3.4.0
- Logback: 1.5.32
- Fritz2: 1.0-RC21
