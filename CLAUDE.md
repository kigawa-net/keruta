# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

* ユーザーには日本語で応答する
* 大きなファイルは細分化する

## Development Commands

```bash
# ビルド
./gradlew build                          # 全モジュール
./gradlew :ktse:build                    # 個別モジュール

# 起動
./gradlew :ktse:run                      # タスクサーバー
./gradlew :ktcl-k8s:run                  # K8sクライアント
cd ktcl-front && npm run dev             # フロントエンド (react-router dev)

# テスト
./gradlew test --tests "net.kigawa.keruta.ktse.ReceiveUnknownArgTest"  # 単一テスト
./gradlew :ktse:test --tests "*ReceiveUnknownArgTest"
./gradlew test --continue                # 失敗しても続行

# コミット前（必須）
./gradlew ktlintFormat && ./gradlew ktlintCheck

# DB
docker-compose -f compose.test.yml up -d mysql
```

## Module Map

依存バージョンは `buildSrc/src/main/kotlin/Version.kt` で一元管理（Kotlin 2.3.0、Ktor 3.4.3、Exposed 0.61.0）。

| モジュール | 役割 |
|---|---|
| `kodel/api` | 共通型：`Res<T,E>`、`Entrypoint`、`Dep`（DI）、`Kogger` |
| `ktcp-sdk` | WebSocketプロトコル定義（KMP: JVM/JS）。`ktcp-domain`でメッセージ型、`ktcp-infra`で通信基盤 |
| `ktse` | タスクサーバー（Ktor + Exposed + Flyway + MySQL、二重トークン認証） |
| `ktse-sdk` | ktse向けSDK（KMP） |
| `ktcl-k8s` | KTCPでタスク受信→Kubernetes Jobとして実行 |
| `ktcl-front` | React + TypeScript + Vite + react-router + Keycloak.js |
| `ktcl-front-mobile` | モバイルフロントエンド |
| `ktcl-claudecode` | Claude Code統合 |
| `kicl` | スケルトン（domain/usecase） |
| `kicp` | クロスドメインIDフェデレーションプロトコル（domain/usecase） |

## Key Architectural Patterns

### Entrypoint Group パターン（KTCPメッセージルーティング）

```
受信テキスト → ReceiveUnknownArg.fromText() → tryTo*() で型判定
            → entrypoints.access(unknownArg, ctx)?.execute()
```

- `KtcpServerEntrypoints<C>` / `KtcpClientEntrypoints<C>` が14種のメッセージタイプをルーティング
- `Entrypoint<I, O, C>` は入力・出力・コンテキスト型でサーバー/クライアントを区別

### Res\<T, E\> パターン（エラーハンドリング）

例外を使わない型安全なエラーハンドリング。`Err`はすべて`Throwable`サブクラス。

```kotlin
// 基本パターン
when (val r = someOperation()) {
    is Res.Err -> return r.convert()   // エラー型を伝搬
    is Res.Ok -> r.value               // 成功値を取得
}

// ユーティリティ
.convertOk { transform(it) }          // 成功値変換
.flatConvertOk { suspendOp(it) }      // Res<Res<T,E>,E> → Res<T,E> (非suspend専用)
.whenOkErr(onOk, onErr)               // 分岐
```

suspend関数のチェーンには `flatConvertOk` が使えないため、`when` を使う。

### 手動DIとFactoryパターン

DIフレームワーク不使用（KMP対応のため）。`{Component}Factory` クラスで依存性を手動組み立て。

### Clean Architecture（domain/usecase分離）

KMPライブラリモジュール（ktcp-sdk、kicp、kicl等）の標準構造：
- `domain`: エンティティ、値オブジェクト、リポジトリ/ポートインターフェース
- `usecase`: アプリケーションサービス（インターフェース + `*Impl`クラス）
- インフラ実装は別モジュール（`*-infra`）

## ktse（タスクサーバー）パッケージ構成

```
auth/     - 二重トークン認証（UserVerifier: OIDC JWT、ProviderVerifier: プロバイダーJWT）
persist/  - Exposed + Flyway によるDB永続化
websocket/- KtcpSession、WebsocketModule
err/      - KtseErr階層
```

**二重トークン認証フロー**: ユーザーJWT（OIDC）+ プロバイダーJWT を両方検証 → `AuthenticatedPersisterSession` 確立。JWKはLRUキャッシュ（最大8発行者）、OIDC Discovery で jwks_url を自動取得。

## ktcl-k8s パッケージ構成

```
connection/ - ConnectionContext、ReceiveClientUnknownArg（型判定）
entrypoint/ - ClientEntrypointsFactory
task/       - TaskReceiver（受信ループ）、TaskExecutor、TaskExecutorFactory
k8s/        - K8sJobExecutor、K8sJobWatcher、K8sClientFactory
auth/       - AuthManager
web/        - KTCL_K8S_WEB_MODE=true で有効化
```

接続フロー: `connectAndCreateSession()` → `authenticate()` → `requestInitialTaskList()` → TaskReceiverループ

## kicp（クロスドメインIDフェデレーション）

`kicp-domain` のポート定義:

| インターフェース | 役割 |
|---|---|
| `JwksRepository` | URL からJWKS取得（キャッシュはinfra実装） |
| `JwtVerifier` | JWTをJWKSで検証 → `TokenClaims` |
| `RegisterTokenRepository` | 登録トークンの保存・検索・削除 |
| `RegisterTokenGenerator` | ランダムな`RegisterToken`生成 |
| `CurrentTimeMs` | 現在時刻（epoch ms）取得 |
| `PeerServerClient` | 相手サーバーへの登録トークン検証呼び出し |

`kicp-usecase` の4ユースケース:

| ユースケース | サーバー側 | 処理 |
|---|---|---|
| `LoginUseCase` | 両方 | providerToken + oidcToken 検証 → `IdentityId` |
| `GetRegisterTokenUseCase` | idServerA | 認証済みIDに登録トークン発行（有効期限付き） |
| `RegisterUseCase` | idServerB | トークン検証 + ピアサーバーへ確認 |
| `VerifyRegisterTokenUseCase` | idServerA | ピアからの登録トークン検証 → 元`IdentityId`返却 |

`IdentityId` は OIDC `issuer:subject` で構成。登録トークンは1回使用で削除（リプレイ防止）。

## Testing

JUnit 5 + Kotlin Test + MockK。テスト関数名はバッククォート形式。

```bash
./gradlew :ktse:test --tests "net.kigawa.keruta.ktse.*Test"
./gradlew cleanTest test  # キャッシュをクリアして再実行
```

## Deployment

```bash
docker build -f Dockerfile_ktse -t harbor.kigawa.net/library/ktse:latest .
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .
docker build -f Dockerfile_ktcl_front -t harbor.kigawa.net/private/ktcl-front:latest .
```

`develop` ブランチへの push で `dev.yml` が自動ビルド・デプロイ（Harbor Registry → kigawa-net-k8s マニフェスト更新）。

## Code Style

- インデント: スペース4、最大行長120文字（ktlint / intellij_idea スタイル）
- エラー型サフィックス: `*Err`（例: `KtcpErr`、`KicpErr`）
- パッケージ: `net.kigawa.keruta.{module}.{layer}.{feature}`
- 完全修飾インポート（ワイルドカード禁止）
- 純粋関数・SOLID原則
