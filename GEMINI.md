# Keruta Project Context

Kotlin Multiplatform を活用したタスク管理システム。WebSocket プロトコル (KTCP) によるリアルタイム通信、Kubernetes Job 実行、OIDC 認証などの機能を提供します。

## 🚀 プロジェクト概要

- **目的**: 高度なタスク管理と自動実行プラットフォームの提供。
- **アーキテクチャ**: Clean Architecture (domain → usecase → infra → application) および Entrypoint パターンを採用。
- **通信**: カスタム WebSocket プロトコル (KTCP) による型安全な双方向メッセージルーティング。
- **認証**: OIDC (Keycloak) および独自セッション管理。
- **実行基盤**: Kubernetes Job との統合。

## 🛠 技術スタック

- **Languages**: Kotlin (Multiplatform), TypeScript, SQL
- **Frameworks**: Ktor (Server), React / React Router v7 (Front), Exposed (ORM), Flyway (Migration)
- **Infrastructure**: Kubernetes, Docker, MySQL, Harbor, ArgoCD
- **Tools**: Gradle (9.5.0), Node.js (24), JUnit 5, MockK, KtLint

## 📂 主要モジュール構成

| モジュール | 説明 |
|-----------|------|
| `kodel` | 共通ライブラリ（`Res` 型、`EntrypointDeferred`、`Kogger`） |
| `ktcp` | KTCP プロトコル定義と基本実装（KMP） |
| `ktse` | Ktor タスクサーバー（コアバックエンド） |
| `ktcl-k8s` | KTCP 受信 → Kubernetes Job 実行クライアント |
| `ktcl-front` | 既存フロントエンド（React + Keycloak.js） |
| `kicl-web` | 次世代フロントエンド（React Router v7 + KMP 共有ロジック） |
| `kicl` | ビジネスロジック共有モジュール（domain/usecase） |
| `kicp` | クロスドメイン ID フェデレーションプロトコル |

## 🏗 開発コマンド

### ビルド & テスト

```bash
./gradlew build                          # 全モジュールビルド
./gradlew test                           # 全テスト実行
./gradlew :ktse:test                     # 個別モジュールテスト
./gradlew ktlintFormat && ./gradlew ktlintCheck  # フォーマット・リント
```

### 実行 (ローカル)

```bash
./gradlew :ktse:run                      # タスクサーバー起動
./gradlew :ktcl-k8s:run                  # K8s クライアント起動
cd ktcl-front && npm run dev             # フロントエンド起動
```

## 📜 開発規約 (重要)

詳細は [CONVENTION.md](CONVENTION.md) および [AGENTS.md](AGENTS.md) を参照してください。

### 1. ブランチ & コミット
- **ブランチ名**: `{prefix}/{description}` (例: `feature/kicl-web-login`)
  - プレフィックス: `feature/`, `fix/`, `doc/`, `refactor/`, `ci/` 等
- **コミット**: Conventional Commits 形式 (`type(scope): description`)
- **PR**: `develop` ブランチへ作成。PRテンプレートを遵守。

### 2. コーディングスタイル
- **Kotlin**:
  - `Res<T, E>` パターンによる型安全なエラーハンドリング（例外は原則使わない）。
  - 手動 Factory パターンによる DI（DIフレームワーク不使用）。
  - 命名: クラスは `PascalCase`、関数/変数は `camelCase`、エラー型は `*Err` サフィックス。
  - テスト: JUnit 5 + MockK。テスト名はバッククォート形式。
- **ディレクトリ構造**: `net.kigawa.keruta.{module}.{layer}.{feature}`

### 3. 実装順序 (Clean Architecture)
必ず以下の順序で実装すること：
1. `domain` 層 (Entity, Repository Interface)
2. `usecase` 層 (Service Interface, Implementation)
3. `infra` 層 (Repository Implementation, DB/HTTP)
4. `application` 層 (Factory, Entrypoint)

## 📖 参考ドキュメント

- [CONVENTION.md](CONVENTION.md): リポジトリ全体の基本規約
- [AGENTS.md](AGENTS.md): AIエージェント向けの詳細ガイドライン
- [doc/architecture.md](doc/architecture.md): アーキテクチャ詳細
- [doc/glossary.md](doc/glossary.md): プロジェクト用語集
- [doc/kicl-web.md](doc/kicl-web.md): 次世代フロントエンド設計

## 🤖 エージェントへの指示

- 作業開始前に `scripts/hooks/` のセットアップを推奨。
- `scripts/check-branch-naming.sh` でブランチ名を確認すること。
- コミット前に必ず `./gradlew ktlintCheck test` を実行すること。
- 日本語でコミュニケーションすること。
