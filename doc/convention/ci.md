# CI作成規約

`.github/workflows/` ディレクトリ配下のGitHub Actionsワークフローを作成・更新する際の規約を定める。
本規約は [CONVENTION.md](../CONVENTION.md) のCI/CDセクションを拡張し、具体的な実装ルールを定める。

## 基本原則

- **ツール**: GitHub Actionsを使用
- **言語**: ワークフロー設定はYAML形式、説明文は日本語
- **文字エンコーディング**: UTF-8
- **改行コード**: LF
- **既存の再利用**: 可重用ワークフロー（reusable workflow）を優先活用

## ファイル命名規則

ワークフローファイルは `.github/workflows/` に配置し、以下の命名規則に従う：

| ワークフロー種別 | 命名規則 | 例 |
|------------------|----------|-----|
| 全体CI | `ci.yml` | `ci.yml` |
| モジュール単体チェック | `{module}-check.yml` | `ktse-check.yml`, `ktcl-front-check.yml` |
| モジュールデプロイ（環境別） | `{module}-{env}.yml` | `ktse-dev.yml`, `kicl-web-stg.yml`, `ktcl-k8s-main.yml` |
| 可重用ワークフロー | 汎用名 | `build-check.yml` |
| 補助ワークフロー | 用途を明記 | `renovate-merge.yml`, `delete-old-merged-branches.yml` |

- モジュール名は `ktse`, `ktcl-k8s`, `ktcl-front`, `kicl-web` 等の公式モジュール名を使用
- 環境名は `dev`, `stg`, `main` を使用（本番は `main`、ステージングは `stg`、開発は `dev`）

## トリガー（on）設定規約

### 基本トリガー

| トリガー種別 | 設定例 | 用途 |
|--------------|--------|------|
| `push` | `branches: [main, develop]` | 主要ブランチへのpush時 |
| `pull_request` | `branches: [develop]` | developへのPR作成・更新時 |
| `workflow_call` | 可重用ワークフローで設定 | 他ワークフローからの呼び出し |

### パスフィルタ（paths）

モジュール単体チェックでは、該当モジュールに関連するファイル変更時のみ実行するよう `paths` を設定する：

```yaml
paths:
  - 'ktse/**'
  - 'Dockerfile_ktse'
  - 'buildSrc/**'
  - '.github/workflows/ktse-check.yml'
  - '.github/workflows/build-check.yml'
```

## 可重用ワークフロー（reusable workflow）規約

### 基本原則

- 共通処理（Dockerビルドチェック等）は `build-check.yml` のような可重用ワークフローにまとめる
- 可重用ワークフローは `workflow_call` トリガーを定義し、入力パラメータ（`inputs`）を設定する

### build-check.yml 入力パラメータ

| パラメータ名 | 型 | 必須 | デフォルト | 説明 |
|--------------|----|------|------------|------|
| `dockerFile` | string | 是 | 无 | Dockerfileのパス |
| `gradleTask` | string | 否 | '' | 実行するGradleタスク（Dockerビルド前に実行） |
| `buildArgs` | string | 否 | '{}' | Dockerビルド引数（JSON形式） |

### 使用例

```yaml
jobs:
  ktse:
    uses: ./.github/workflows/build-check.yml
    with:
      dockerFile: './Dockerfile_ktse'
      gradleTask: ':ktse:shadowJar -x test'
```

## ジョブ（jobs）規約

### 実行環境

- 基本は `runs-on: ubuntu-latest` を使用
- 必要に応じて `runs-on: [self-hosted, linux]` 等の特定環境を指定（現在は未使用）

### サービス（services）

テストで必要なサービス（MySQL等）は `services` で定義する：

```yaml
services:
  mysql:
    image: mysql:9.7
    env:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: keruta_test
      MYSQL_USER: keruta
      MYSQL_PASSWORD: keruta
    ports:
      - 3306:3306
    options: >-
      --health-cmd "mysqladmin ping -h localhost -u keruta -pkeruta"
      --health-interval 5s
      --health-timeout 3s
      --health-retries 10
```

## ステップ（steps）規約

### 基本ステップ順序

1. **チェックアウト**: `actions/checkout@v6`
2. **環境セットアップ**: `actions/setup-java@v5`, `actions/setup-node@v6` 等
3. **キャッシュ設定**: `actions/cache@v5`（Gradle、Node.js等）
4. **ビルド・テスト**: `./gradlew test`, `npm run test` 等
5. **Dockerビルド**: `docker/build-push-action@v7`（可重用ワークフローで実行）

### バージョン固定

使用するアクションはバージョンを固定する（例: `@v6`, `@v5`）。

### Gradle設定

- Gradleキャッシュを有効化する
- デーモンは無効化する（`GRADLE_OPTS: -Dorg.gradle.daemon=false`）
- 設定キャッシュを有効化する（`enable-configuration-cache: true`）

## キャッシュ戦略

| 対象 | アクション | キャッシュキー | 説明 |
|------|-----------|--------------|------|
| Gradle | `actions/cache@v5` | `gradle-${{ runner.os }}-${{ hashFiles('gradle/wrapper/gradle-wrapper.properties') }}` | `~/.gradle/caches`, `~/.gradle/wrapper` をキャッシュ |
| Node.js | `actions/setup-node@v6` の `cache` パラメータ | `package-lock.json` のハッシュ | `node_modules` をキャッシュ |
| Docker | `docker/build-push-action@v7` の `cache-from`/`cache-to` | `type=gha` | GitHub Actionsのキャッシュを使用 |

## 環境変数とシークレット管理

### 環境変数

- 公開可能な変数は `env` で直接設定
- モジュール固有の変数は `working-directory` と組み合わせて設定

### シークレット

- データベースパスワード、APIキー等の敏感情報はGitHub Secretsに保存し、`secrets` コンテキストで参照する
- 例: `${{ secrets.HARBOR_USERNAME }}`

## テスト実行規約

### Gradleモジュール

- 全テスト: `./gradlew test allTests`
- 単一モジュール: `./gradlew :ktse:test`
- テストDB設定: `TEST_DB_JDBC_URL`, `TEST_DB_USERNAME`, `TEST_DB_PASSWORD` を環境変数で渡す

### Node.jsモジュール

- `npm run test` を実行
- 必要な環境変数（`VITE_*` 等）を設定する

## デプロイワークフロー規約

### Dockerイメージビルド

- Dockerfileはプロジェクトルートに `Dockerfile_{module}` の形式で配置
- Harbor Registryへのプッシュは `develop` または `main` ブランチpush時にのみ実行
- イメージタグは環境に応じて設定（例: `latest`, `dev`, `stg`, `main`）

### k8sマニフェスト更新

- デプロイ後は `kigawa-net/k8s-manifests` リポジトリのマニフェストを更新
- ArgoCDが自動的にデプロイを実行

## 既存規約との整合性

- ワークフローの変更は [CONVENTION.md](../CONVENTION.md) のブランチ戦略に従い、PRを通じて `develop` にマージする
- コミットメッセージは [CONVENTION.md](../CONVENTION.md) のコミットメッセージ規約（Conventional Commits）に従う（`ci:` プレフィックスを使用）
- 例: `ci: add ktcl-claudecode-check workflow`

## 更新ルール

1. **新規作成**: 新モジュール追加時に該当モジュールの `-check.yml` と環境別デプロイワークフローを作成
2. **既存更新**: ワークフロー変更時は関連するモジュールの動作を確認し、CIが通過することを保証
3. **ドキュメント更新**: 本規約を変更した場合は、関連ドキュメント（[CONVENTION.md](../CONVENTION.md) 等）も同時に更新
4. **PR作成**: ワークフローの変更は専用のPRを作成し、レビューを受ける
