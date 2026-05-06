# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## ⚠️ 規約遵守の重要事項

**すべての作業において以下の規約を必ず遵守すること：**
- [CONVENTION.md](CONVENTION.md) - リポジトリ全体の規約（必読）
- [doc/pr-convention.md](doc/pr-convention.md) - PR作成規約
- [doc/ci-convention.md](doc/ci-convention.md) - CI/CD規約

### 作業前のセットアップ（初回のみ）
```bash
# Git hooks をセットアップして規約チェックを自動化
cp scripts/hooks/pre-commit.template .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
cp scripts/hooks/pre-push.template .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

### 規約チェックコマンド
```bash
# ブランチ名の手動チェック
scripts/check-branch-naming.sh [ブランチ名]

# コミット前の必須チェック
./gradlew ktlintFormat && ./gradlew ktlintCheck
./gradlew test
```

* ユーザーには日本語で応答する
* 大きなファイルは細分化する

## Development Commands

### セットアップ（初回のみ）
```bash
# Git hooks をセットアップして規約チェックを自動化
cp scripts/hooks/pre-commit.template .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
cp scripts/hooks/pre-push.template .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

### ビルド
```bash
./gradlew build                          # 全モジュールビルド
./gradlew :ktse:build                    # 個別モジュール
```

### 起動
```bash
./gradlew :ktse:run                      # タスクサーバー起動
./gradlew :ktcl-k8s:run                  # K8sクライアント起動
cd ktcl-front && npm run dev             # フロントエンド起動 (react-router dev)
```

### テスト
```bash
./gradlew test                           # 全テスト実行
./gradlew test --tests "net.kigawa.keruta.ktse.ReceiveUnknownArgTest"  # 単一テスト
./gradlew test --tests "*ReceiveUnknownArgTest"  # クラス名指定
./gradlew test --continue                # 失敗しても続行
./gradlew cleanTest test                  # キャッシュをクリアして再実行
```

### リント・フォーマット（コミット前必須）
```bash
./gradlew ktlintFormat                   # 自動フォーマット
./gradlew ktlintCheck                    # リントチェック
```

### DB
```bash
docker-compose -f compose.test.yml up -d mysql   # テスト用MySQL起動
```

### 規約チェック
```bash
# ブランチ名の手動チェック
scripts/check-branch-naming.sh [ブランチ名]

# PR作成時はテンプレートを使用
# .github/pull_request_template.md を参照
```

## Module Map

依存バージョンは `buildSrc/src/main/kotlin/Version.kt` で一元管理。

**主要バージョン**（詳細は [CONVENTION.md](CONVENTION.md#6-1-環境) 参照）:
- Kotlin: 2.3.0
- Ktor: 3.4.0
- Java: Eclipse Temurin 25
- Node.js: 24
- Gradle: 9.5.0

| モジュール | 役割 |
|---|---|
| `kodel` | 共通ライブラリ（Res型、EntrypointDeferred、Kogger） |
| `ktcp` | WebSocketプロトコル（Kotlin Multiplatform対応: domain/infra） |
| `ktse` | Ktorタスクサーバー（Exposed/Flyway/MySQL、二重トークン認証） |
| `ktcl-k8s` | KTCPでタスク受信→Kubernetes Jobとして実行 |
| `ktcl-front` | フロントエンド（React + TypeScript + Vite + Keycloak.js） |
| `kicl-web` | 次世代フロントエンド（React Router v7 + KMP共有ロジック） |
| `kicl` | Kotlin Multiplatformモジュール（domain/usecase） |
| `kicp` | クロスドメインIDフェデレーションプロトコル（domain/usecase） |
| `ktcl-claudecode` | Claude Code統合 |
| `ktcl-front-mobile` | モバイルフロントエンド |

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

詳細は [CONVENTION.md](CONVENTION.md#4-コードスタイル) を参照。

- インデント: スペース4、最大行長120文字（ktlint / intellij_idea スタイル）
- エラー型サフィックス: `*Err`（例: `KtcpErr`、`KicpErr`）
- パッケージ: `net.kigawa.keruta.{module}.{layer}.{feature}`
- 完全修飾インポート（ワイルドカード禁止）
- 純粋関数・SOLID原則
- KDocコメントで文書化（日本語）
- 日本語使う

## 📋 規約遵守チェックリスト

作業を開始する前に、以下の規約を確認すること：

### ブランチ作成時
- [ ] ブランチ名が規約に従っているか（`feature/`, `fix/`, `docs/` など）
- [ ] チェックスクリプトで確認: `scripts/check-branch-naming.sh [ブランチ名]`

### コミット前
- [ ] コミットメッセージが [Conventional Commits](https://www.conventionalcommits.org/) 形式か
- [ ] `./gradlew ktlintFormat && ./gradlew ktlintCheck` を実行済みか
- [ ] `./gradlew test` ですべてのテストが通るか
- [ ] 秘密情報（認証情報、APIキー等）が含まれていないか

### PR作成前
- [ ] PRテンプレート（`.github/pull_request_template.md`）に従って記入済みか
- [ ] 変更の粒度は適切か（1 PR = 1 機能/修正）
- [ ] CIが全て通過しているか
- [ ] [doc/pr-convention.md](doc/pr-convention.md) を確認済みか

### 参考ドキュメント
- [CONVENTION.md](CONVENTION.md) - リポジトリ全体の規約（必読）
- [doc/pr-convention.md](doc/pr-convention.md) - PR作成規約
- [doc/ci-convention.md](doc/ci-convention.md) - CI/CD規約
- [doc/development-setup.md](doc/development-setup.md) - 開発環境セットアップ
- [doc/glossary.md](doc/glossary.md) - 用語集
