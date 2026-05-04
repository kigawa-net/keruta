# リポジトリ規約

keruta リポジトリの開発に関する規約を定める。

## 1. ブランチ戦略

### 1-1. 主要ブランチ

| ブランチ | 用途 | 保護 |
|----------|------|------|
| `main` | 本番環境 | PR経由のみ、レビュー必須 |
| `develop` | 開発環境（デフォルト） | PR経由のみ |

### 1-2. 作業ブランチ

作業は機能ごとのブランチで行い、完了後に `develop` へPRを作成する。

命名規則: `{prefix}/{description}`

| プレフィックス | 用途 | 例 |
|----------------|------|-----|
| `feature/` | 新機能開発 | `feature/kicl-web-user-settings` |
| `fix/` | バグ修正 | `fix/kicl-web-nav-on-index` |
| `fixes/` | 複数修正を含むブランチ | `fixes/ci` |
| `doc/`, `docs/` | ドキュメント追加・更新 | `doc/convention` |
| `refactor/` | リファクタリング | `refactor/decode-frame-errors` |
| `changes/` | 既存機能の変更 | `changes/ktcl-front/add-auth` |
| `renovate/` | Renovateによる自動更新 | `renovate/eslint-monorepo` |

### 1-3. ブランチのライフサイクル

- 作業ブランチはPRマージ後に削除
- 30日以上放置されたマージ済みブランチは自動削除
- `develop` は常にデプロイ可能な状態を維持

### 1-4. ブランチ名の自動チェック

- CIでブランチ名規約を自動チェック（`scripts/check-branch-naming.sh`）
- ローカルでのコミット・プッシュ時もGitフックでチェック
- 規約に合わないブランチ名はCI失敗となる

## 2. コミットメッセージ規約

Conventional Commits に基づく形式を採用。

### 2-1. 基本形式

```
type(scope): 説明
```

| 項目 | 説明 |
|------|------|
| `type` | 変更の種類（後述） |
| `scope` | 対象モジュール名（任意） |
| `説明` | 何を行ったか（日本語または英語） |

### 2-2. タイプ一覧

| タイプ | 用途 | 例 |
|--------|------|-----|
| `feat` | 新機能 | `feat: Implement task detail screen` |
| `fix` | バグ修正 | `fix(kicl-web): ナビゲーションが表示されない問題を修正` |
| `doc`, `docs` | ドキュメント | `doc: ドキュメント作成規約を追加` |
| `refactor` | リファクタリング | `refactor(ktcl-front): introduce service layer` |
| `ci` | CI/CD設定変更 | `ci: optimize workflow performance` |
| `chore` | メンテナンスタスク | `chore(deps): update dependency @types/node` |
| `revert` | 変更の取り消し | `revert: kotlinx-serialization-json 1.11.0 -> 1.10.0` |
| `test` | テスト追加・修正 | `test: add unit tests for K8sJobExecutor` |

### 2-3. スコープ例

- `(kicl-web)`, `(ktcl-front)`, `(ktse)`, `(ktcl-k8s)`, `(kodel)`, `(ktcp)`, `(kicl)`, `(deps)`

### 2-4. 記述ルール

- 説明は現在形で始める（「追加した」→「追加」）
- 最初の文字は小文字で始める（日本語の場合は問わない）
- 末尾にピリオドは付けない
- 1行目は50文字以内を推奨

## 3. Pull Request

### 3-1. 作成ルール

- 変更内容は機能単位で分割
- `develop` ブランチをベースにする
- PRテンプレートに従って説明文を記入
- CIが全て通過していること

### 3-2. PR説明文

以下の項目を記載する:

```markdown
## 概要
変更の目的を1-2行で簡潔に説明。

## 主な変更点
- 変更点1
- 変更点2

## 影響範囲
- 影響を受けるモジュール・機能
- 後方互換性のある/なし

## 関連
- 関連Issue番号
- 関連PR番号
```

### 3-3. レビュー

- 最低1名のレビュー承認が必要
- 作成者自身によるマージは可（レビュー通過後）
- レビュー指摘は修正コミットで対応（force push可）

### 3-4. マージ

- Squash Merge を使用
- マージ時のコミットメッセージはPRタイトルを使用
- マージ後、作業ブランチは削除

## 4. コードスタイル

### 4-1. 共通

- エンコーディング: UTF-8
- 改行コード: LF
- インデント: スペース4
- 最大行長: 120文字
- 末尾の空白: 削除
- 末尾の改行: 必須

詳細は `.editorconfig` を参照。

### 4-2. Kotlin

| 種類 | 規則 | 例 |
|------|------|-----|
| クラス/インターフェース | PascalCase | `TaskExecutorFactory` |
| 関数/変数 | camelCase | `create()`, `config` |
| 定数 | UPPER_SNAKE_CASE | `KOTLIN = "2.3.0"` |
| エラー型 | `*Err` サフィックス | `KtcpErr`, `BackendErr` |
| テスト関数 | バッククォート | `` `testServerMsgTypeStringComparison()` `` |
| パッケージ | `net.kigawa.keruta.{module}.{layer}.{feature}` | |

- KtLint (`intellij_idea` スタイル) に準拠
- 完全修飾名を使用（ワイルドカードインポート禁止）
- DIは手動Factoryパターン（DIフレームワーク不使用）
- エラーハンドリングは `Res<T, E>` パターンを推奨

### 4-3. TypeScript/React

- ESLint に準拠
- コンポーネントは関数コンポーネント
- 型定義は必ず明示

## 5. テスト

### 5-1. 実行方法

```bash
./gradlew test                           # 全テスト実行
./gradlew test --tests "*ClassNameTest"  # 単一テストクラス
```

### 5-2. ルール

- 新機能追加時はテストをセットで実装
- バグ修正時は再現テストを追加
- CIで全テストが通過すること

## 6. CI/CD

### 6-1. 環境

| 項目 | バージョン |
|------|-----------|
| Java | Eclipse Temurin 25 |
| Kotlin | 2.3.0 |
| Node.js | 24 |
| Gradle | 9.5.0 |
| MySQL | 9.7 |

### 6-2. ワークフロー

| ワークフロー | トリガー | 用途 |
|-------------|----------|------|
| `ci.yml` | `main`, `develop` push | 全モジュールのビルド・テスト |
| `*-check.yml` | PR (develop) | 対象モジュールのビルド検証 |
| `*-dev.yml` | `develop` push | dev環境デプロイ |
| `*-main.yml` | `main` push | 本番環境デプロイ |

### 6-3. Renovate

- 毎晩19:00実行
- PRは `develop` へ自動マージ（CI通過後）
- `renovate-merge.yml` により自動化

## 7. バージョニング

### 7-1. 依存バージョン

`buildSrc/src/main/kotlin/Version.kt` で一元管理。

```kotlin
object Version {
    const val KOTLIN = "2.3.0"
    const val KTOR = "3.4.0"
    // ...
}
```

### 7-2. プロジェクトバージョン

- 環境変数 `VERSION` から取得
- 未設定時は `"dev"`
- gitタグは使用しない（ブランチ名で環境区別）

## 8. デプロイフロー

### 8-1. パイプライン

```
develop へのpush
  ↓
CI実行（テスト・ビルド）
  ↓
Dockerイメージビルド → Harbor Registry (harbor.kigawa.net)
  ↓
k8sマニフェスト更新 (kigawa-net/k8s-manifests)
  ↓
ArgoCD 自動デプロイ
```

### 8-2. Harborプロジェクト

| プロジェクト | 対象イメージ |
|-------------|-------------|
| `library` | ktse, ktcl-k8s |
| `private` | ktcl-front, kicl-web |

### 8-3. 環境変数

| 変数 | 用途 |
|------|------|
| `DB_JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD` | 本番DB接続 |
| `TEST_DB_*` | テストDB接続 |
| `KTCL_K8S_WEB_MODE` | Webモード切替 |
| `VERSION` | プロジェクトバージョン |

## 9. ドキュメント

- 日本語で記述（技術用語は英語可）
- 新ドキュメント作成時は `doc/README.md` の一覧に追加
- 詳細は `doc/convention.md` を参照

## 10. モジュール構成

| モジュール | 説明 |
|-----------|------|
| `kodel` | 共通ライブラリ（Res型、EntrypointDeferred、Kogger） |
| `ktcp` | WebSocketプロトコル（Kotlin Multiplatform対応） |
| `ktse` | Ktorタスクサーバー（Exposed/Flyway/MySQL） |
| `ktcl-k8s` | K8sクライアント（KTCP受信→Kubernetes Job実行） |
| `ktcl-front` | フロントエンド（React + Keycloak.js） |
| `kicl-web` | 次世代フロントエンド（React Router v7 + KMP） |
| `kicl` | KMPモジュール（domain/usecase） |
| `ktcl-claudecode` | Claude Code統合 |
