# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## ⚠️ 規約遵守の重要事項

**すべての作業において以下の規約を必ず遵守すること：**
- [CONVENTION.md](CONVENTION.md) - リポジトリ全体の規約（必読）
- [doc/pr-convention.md](doc/pr-convention.md) - PR作成規約
- [doc/ci-convention.md](doc/ci-convention.md) - CI/CD規約

## 🚨 Issue作成からPR作成までの必須フロー

> **手順を省略・スキップすることは禁止。必ず以下の順番で実施すること。**

### Step 0: Issue作成（実装前に必須）

```bash
# Issue を作成してから実装を開始する
gh issue create \
  --title "type(scope): 実装内容の説明" \
  --body "$(cat <<'EOF'
## 概要
実装する機能や修正の目的を説明。

## 実装内容
- 実装内容1
- 実装内容2

## 完了条件
- [ ] 条件1
- [ ] 条件2
EOF
)" \
  --label "enhancement"

# Issue番号を確認（後でPR・ブランチに関連付ける）
gh issue list --state open
```

- 対応するIssueが存在しない場合、実装を開始してはならない
- ラベルは変更種別に応じて選択: `enhancement`（新機能）、`bug`（修正）、`documentation`（ドキュメント）、`refactoring`（リファクタリング）

### Step 1: ブランチ作成（実装前に必須）

```bash
# 命名規則: {prefix}/{module}-{feature}
# 例: feat/kicp-peer-client, fix/ktse-auth-token, docs/agents-convention
git checkout -b feat/{module}-{feature}
scripts/check-branch-naming.sh $(git branch --show-current)
```

- ブランチ名チェックがエラーになった場合はブランチを作り直すこと
- `develop`・`main` への直接コミットは禁止

### Step 2: 実装（レイヤー順序を守ること）

```
domain層 → usecase層 → infra層 → application層
```

- 各層の実装と同時にテストを追加すること
- バグ修正時は先に再現テストを追加してから修正すること

### Step 3: コードスタイル確認（コミット前に必須）

```bash
./gradlew ktlintFormat   # 自動フォーマット
./gradlew ktlintCheck    # 違反がないことを確認
```

ktlintCheck がエラーの状態でコミットしてはならない。

### Step 4: テスト実行（コミット前に必須）

```bash
./gradlew :{module}:test    # 変更モジュールのテスト
./gradlew test               # 影響範囲が広い場合は全テスト
```

テストが失敗している状態でコミットしてはならない。

### Step 5: ビルド確認

```bash
./gradlew :{module}:build
```

### Step 6: コミット

```bash
# Conventional Commits 形式: type(scope): 説明
git commit -m "feat(kicp): peer client を実装"
```

- `type`: feat / fix / docs / refactor / ci / chore / test / revert
- `scope`: 対象モジュール名
- 秘密情報（認証情報・APIキー）が含まれていないことを確認

### Step 7: PR 作成（必須手順）

```bash
# ベースブランチは必ず develop を指定する
# Closes #<Issue番号> で対応Issueを自動クローズ
gh pr create --base develop \
  --title "feat(module): 変更内容の説明" \
  --body "$(cat <<'EOF'
## 概要
変更の目的を1-2行で簡潔に説明。

## 主な変更点
- 変更点1
- 変更点2

## 影響範囲
- 影響を受けるモジュール・機能

## 関連
- Closes #<Issue番号>
EOF
)"
```

PR作成前チェック:
- [ ] ベースブランチが `develop` であること
- [ ] `.github/pull_request_template.md` の全項目を記入済み
- [ ] CI（GitHub Actions）が全て通過していること
- [ ] 1 PR = 1 機能（または 1 修正）の粒度であること
- [ ] `Closes #<Issue番号>` で対応Issueを関連付けていること

---

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

> 実装の各フェーズで以下を確認すること。詳細は「[Issue作成からPR作成までの必須フロー](#-issue作成からpr作成までの必須フロー)」を参照。

### Issue作成時（Step 0）
- [ ] 対応するIssueが存在するか（存在しない場合は先に作成）
- [ ] `gh issue create` でタイトル・本文・ラベルを記入済みか
- [ ] 完了条件が明確に記載されているか

### ブランチ作成時（Step 1）
- [ ] ブランチ名が規約に従っているか（`feat/`, `fix/`, `docs/` など）
- [ ] `scripts/check-branch-naming.sh $(git branch --show-current)` でエラーがないか

### コミット前（Step 3〜6）
- [ ] `./gradlew ktlintFormat` を実行済みか
- [ ] `./gradlew ktlintCheck` がエラーなしで通過するか
- [ ] `./gradlew :{module}:test` ですべてのテストが通るか
- [ ] コミットメッセージが [Conventional Commits](https://www.conventionalcommits.org/) 形式か
- [ ] 秘密情報（認証情報、APIキー等）が含まれていないか

### PR作成前（Step 7）
- [ ] ベースブランチは `develop`（`--base develop` を明示的に指定）
- [ ] PRテンプレート（`.github/pull_request_template.md`）の全項目を記入済みか
- [ ] 変更の粒度は適切か（1 PR = 1 機能/修正）
- [ ] CIが全て通過しているか
- [ ] `Closes #<Issue番号>` で対応Issueを関連付けているか
- [ ] [doc/pr-convention.md](doc/pr-convention.md) を確認済みか

### 参考ドキュメント
- [CONVENTION.md](CONVENTION.md) - リポジトリ全体の規約（必読）
- [doc/pr-convention.md](doc/pr-convention.md) - PR作成規約
- [doc/ci-convention.md](doc/ci-convention.md) - CI/CD規約
- [doc/development-setup.md](doc/development-setup.md) - 開発環境セットアップ
- [doc/glossary.md](doc/glossary.md) - 用語集
