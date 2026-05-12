# AGENTS.md

This file provides guidance for AI agents working with this codebase.
日本語で話す。

リポジトリ全体の規約は [CONVENTION.md](CONVENTION.md) を参照。

## ⚠️ 規約遵守の重要事項

**すべての作業において以下の規約を必ず遵守すること：**
- [CONVENTION.md](CONVENTION.md) - リポジトリ全体の規約（必読）
- [doc/pr-convention.md](doc/pr-convention.md) - PR作成規約
- [doc/ci-convention.md](doc/ci-convention.md) - CI/CD規約

> **実装を開始する前に「[実装からPR作成までの必須フロー](#実装からpr作成までの必須フロー)」を確認し、手順通りに進めること。**

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

## 開発コマンド

### ビルド

```bash
./gradlew build                          # 全モジュールビルド
./gradlew :ktse:build                    # 個別モジュール
```

### 起動

```bash
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

| モジュール | 説明 |
|-----------|------|
| `kodel` | 共通ライブラリ（Res型、EntrypointDeferred、Kogger） |
| `ktcp` | WebSocketプロトコル（Kotlin Multiplatform対応: domain/infra） |
| `ktse` | Ktorタスクサーバー（Exposed/Flyway/MySQL、二重トークン認証） |
| `ktcl-k8s` | KTCPでタスク受信しKubernetes Jobとして実行 |
| `ktcl-front` | React+TypeScript+Vite+Keycloak.js（既存フロントエンド） |
| `kicl` | Kotlin Multiplatformモジュール（domain/usecase）- JSライブラリ出力 |
| `kicl-web` | React Router v7 + Kotlin Multiplatform共有ロジック（次世代フロントエンド） |
| `kicp` | クロスドメインIDフェデレーションプロトコル（domain/usecase） |
| `ktcl-claudecode` | Claude Code統合 |
| `ktcl-front-mobile` | モバイルフロントエンド |

詳細ドキュメント: `doc/` 配下参照（特に `doc/kicl-web.md`）

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

## 実装手順規約

### レイヤー実装順序

Clean Architecture の原則に従い、以下の順序で実装する。

```
1. domain層    エンティティ・値オブジェクト・リポジトリ/ポートインターフェース
     ↓
2. usecase層   アプリケーションサービス（インターフェース定義 → *Impl実装）
     ↓
3. infra層     リポジトリ/ポートの具体実装（DB・HTTP等の外部依存）
     ↓
4. application 層   Factoryクラスによるエントリポイント・DI組み立て
```

- 上位層（domain）が下位層（infra）に依存しないこと
- `*Impl` クラスは usecase インターフェースを実装し、infra 依存を受け取る
- Factory クラスで依存性を手動組み立て（DIフレームワーク不使用）

### 新モジュール作成手順

KMPライブラリモジュール（例: `foo-domain`, `foo-usecase`）を追加する場合。

**1. ディレクトリ・ファイル作成**

```
foo/
├── foo-domain/
│   ├── build.gradle.kts
│   └── src/commonMain/kotlin/net/kigawa/keruta/foo/domain/
│       └── FooDomain.kt
└── foo-usecase/
    ├── build.gradle.kts
    └── src/commonMain/kotlin/net/kigawa/keruta/foo/usecase/
```

**2. `build.gradle.kts` テンプレート（domain層）**

```kotlin
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":kodel:api"))
    }
}
```

usecase層は `api(project(":foo:foo-domain"))` を依存に追加する。

**3. `settings.gradle.kts` へのモジュール登録**

```kotlin
includeDsl {
    includeIfExistsAndGroup("foo") {
        includeIfExists("domain")   // :foo:foo-domain
        includeIfExists("usecase")  // :foo:foo-usecase
    }
}
```

**4. パッケージ命名**

`net.kigawa.keruta.{module}.{layer}.{feature}` の形式に従う。

### 既存モジュールへの機能追加

1. **domain層** — 必要なエンティティ・インターフェースを追加/変更する
2. **usecase層** — 新しいユースケースクラス（インターフェース + `*Impl`）を追加する
3. **infra/application層** — インターフェース実装・Factoryへの登録を行う
4. **テスト** — 各層に対応するテストを同時に追加する

既存インターフェースを変更する場合は、すべての実装クラス・Factoryクラスへの影響を確認する。

### テスト実装規約

- テストクラス名: `{ClassName}Test`（例: `LoginUseCaseImplTest`）
- テスト関数名: バッククォート形式の日本語または英語
  ```kotlin
  @Test
  fun `正常なトークンでログインできる`() { ... }
  ```
- KMPモジュールのテスト配置: `src/commonTest/kotlin/`
- JVM専用モジュールのテスト配置: `src/jvmTest/kotlin/`
- モック: MockK を使用（`mockk<Interface>()` / `coEvery { } returns`）
- 新機能追加時はテストをセットで実装する
- バグ修正時は再現テストを先に追加してから修正する

### 実装からPR作成までの必須フロー

> ⚠️ **以下の手順を必ず順番通りに実行すること。手順を省略・スキップすることは禁止。**

#### ステップ 1: ブランチ作成（実装開始前に必須）

```bash
# ブランチ命名規則: {prefix}/{module}-{feature}
# 例: feat/kicp-peer-client-impl, fix/ktse-auth-token, docs/agents-convention
git checkout -b feat/{module}-{feature}

# ブランチ名が規約に従っているか確認
scripts/check-branch-naming.sh $(git branch --show-current)
```

- ブランチ名が規約に合格しない場合はブランチを作り直すこと
- `develop` や `main` への直接コミットは禁止

#### ステップ 2: 実装

Clean Architecture の原則に従い、以下の順序で実装する。順序を逆にしてはならない。

```
domain層 → usecase層 → infra層 → application層
```

- 各層の実装と同時にテストを追加すること
- バグ修正時は再現テストを先に追加してから修正すること

#### ステップ 3: コードスタイル確認（コミット前に必須）

```bash
./gradlew ktlintFormat   # 自動フォーマット適用
./gradlew ktlintCheck    # スタイル違反がないことを確認（エラーがあればコミット不可）
```

ktlintCheck がエラーになっている状態でのコミットは禁止。

#### ステップ 4: テスト実行（コミット前に必須）

```bash
./gradlew :{module}:test           # 変更モジュールのテスト
./gradlew test                     # 全テスト（影響範囲が広い場合）
```

テストが失敗している状態でのコミットは禁止。

#### ステップ 5: ビルド確認

```bash
./gradlew :{module}:build
```

#### ステップ 6: コミット

```bash
# Conventional Commits 形式: type(scope): 説明
git commit -m "feat(kicp): peer client を実装"
```

コミットメッセージ規則:
- `type`: feat / fix / docs / refactor / ci / chore / test / revert
- `scope`: 対象モジュール名（例: `ktse`, `kicl-web`, `kicp`）
- 説明は現在形、末尾ピリオドなし

秘密情報（認証情報・APIキー等）が含まれていないことを確認すること。

#### ステップ 7: PR 作成（必須手順）

```bash
# ベースブランチは必ず develop を指定する
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
- 関連Issue/PR番号
EOF
)"
```

PR 作成前のチェック:
- [ ] ベースブランチが `develop` であること（`main` への直接 PR は本番リリース時のみ）
- [ ] PRテンプレート（`.github/pull_request_template.md`）の全項目を記入済み
- [ ] CI（GitHub Actions）が全て通過していること
- [ ] 1 PR = 1 機能（または 1 修正）の粒度になっていること

## デプロイ

```bash
docker build -f Dockerfile_ktse -t harbor.kigawa.net/library/ktse:latest .
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .
docker build -f Dockerfile_ktcl_front -t harbor.kigawa.net/private/ktcl-front:latest .
```

developブランチへのpushで `dev.yml` が自動ビルド・デプロイ（Harbor Registry → kigawa-net-k8s マニフェスト更新）

## 依存バージョン

詳細は [CONVENTION.md](CONVENTION.md#6-1-環境) 参照。

- **Kotlin**: 2.3.0
- **Ktor**: 3.4.0
- **Java**: Eclipse Temurin 25
- **Node.js**: 24
- **Gradle**: 9.5.0
- **MySQL**: 9.7

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
