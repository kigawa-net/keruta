# 開発手順規約

keruta プロジェクトにおける開発作業の標準手順を定める。
すべての開発者は実装着手からPRマージまで、以下の手順に従うこと。

各フェーズの詳細ルールは関連規約を参照:
- [CONVENTION.md](../CONVENTION.md) — ブランチ・コミット・コードスタイル全般
- [doc/pr-convention.md](pr-convention.md) — PR作成・レビュー・マージ
- [doc/ci-convention.md](ci-convention.md) — CI/CDワークフロー

---

## 手順概要

```
Step 1: ブランチ作成
Step 2: 実装（レイヤー順）
Step 3: コードフォーマット
Step 4: テスト実行
Step 5: ビルド確認
Step 6: コミット
Step 7: PR作成
Step 8: レビュー対応
Step 9: マージ
```

手順はすべて順番通りに実施すること。スキップ・省略は禁止。

---

## Step 1: ブランチ作成

実装を開始する前に必ず作業ブランチを作成する。

```bash
git checkout develop
git pull origin develop
git checkout -b {prefix}/{description}
scripts/check-branch-naming.sh $(git branch --show-current)
```

命名規則: `{prefix}/{description}`

| プレフィックス | 用途 | 例 |
|---------------|------|-----|
| `feature/` | 新機能開発 | `feature/kicl-web-user-settings` |
| `fix/` | バグ修正 | `fix/ktse-auth-token` |
| `fixes/` | 複数修正を含むブランチ | `fixes/ci` |
| `doc/`, `docs/` | ドキュメント追加・更新 | `doc/development-convention` |
| `refactor/` | リファクタリング | `refactor/ktcp-decode-errors` |
| `changes/` | 既存機能の変更 | `changes/ktcl-front-add-auth` |

**禁止事項**:
- `develop` / `main` への直接コミット
- `scripts/check-branch-naming.sh` がエラーを返すブランチ名での作業続行

---

## Step 2: 実装

### レイヤー実装順序

KMPライブラリモジュール（ktcp-sdk、kicp、kicl 等）では以下の順序で実装する:

```
domain層 → usecase層 → infra層 → application層
```

各層を飛ばして上位層から実装してはならない。

### テストの追加タイミング

| ケース | テスト追加のタイミング |
|--------|----------------------|
| 新機能実装 | 実装と同時に追加 |
| バグ修正 | **修正前**に再現テストを追加し、失敗を確認してから修正 |
| リファクタリング | 既存テストが通ることを確認しながら進める |

### アーキテクチャ上の注意

- エラーハンドリングは `Res<T, E>` パターンを使用（例外禁止）
- DIはFactoryパターンで手動組み立て（DIフレームワーク不使用）
- ワイルドカードインポート禁止（完全修飾名を使用）

---

## Step 3: コードフォーマット

コミット前に必ず実行する。

```bash
./gradlew ktlintFormat   # 自動フォーマット
./gradlew ktlintCheck    # 違反がないことを確認
```

`ktlintCheck` がエラーを返す状態でコミットしてはならない。

TypeScript/React の変更がある場合:

```bash
cd ktcl-front && npm run lint
# または
cd kicl-web && npm run lint
```

---

## Step 4: テスト実行

```bash
# 変更モジュールのみ
./gradlew :{module}:test

# 影響範囲が広い場合（他モジュールのAPIを変更した場合など）
./gradlew test

# テストキャッシュを無効化して再実行
./gradlew cleanTest test
```

テストが失敗している状態でコミットしてはならない。

テスト用MySQLが必要な場合:

```bash
docker-compose -f compose.test.yml up -d mysql
```

---

## Step 5: ビルド確認

```bash
./gradlew :{module}:build
```

ビルドが失敗している状態でコミットしてはならない。

---

## Step 6: コミット

### コミットメッセージ形式

Conventional Commits 形式を使用:

```
type(scope): 説明
```

| type | 用途 |
|------|------|
| `feat` | 新機能 |
| `fix` | バグ修正 |
| `docs` / `doc` | ドキュメント |
| `refactor` | リファクタリング |
| `ci` | CI/CD設定変更 |
| `chore` | 依存関係更新等のメンテナンス |
| `test` | テスト追加・修正 |
| `revert` | 変更の取り消し |

例:
```bash
git commit -m "feat(kicp): peer client を実装"
git commit -m "fix(ktse): WebSocket接続が切れる問題を修正"
git commit -m "docs: 開発手順規約を追加"
```

### コミット前の最終確認

- [ ] `ktlintCheck` がエラーなしで通過している
- [ ] 変更モジュールのテストがすべて通過している
- [ ] 秘密情報（認証情報、APIキー等）が含まれていない

---

## Step 7: PR作成

### 事前チェック

- [ ] ベースブランチが `develop` であること
- [ ] CIが全て通過していること
- [ ] 1 PR = 1機能（または1修正）の粒度であること
- [ ] `doc/pr-convention.md` を確認済みであること

### PR作成コマンド

```bash
gh pr create --base develop \
  --title "type(scope): 変更内容の説明" \
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

`--base develop` の省略は禁止。省略した場合、デフォルトブランチへのPRが誤って作成される。

---

## Step 8: レビュー対応

### レビュー指摘への対応

1. 指摘内容を理解し、必要であれば質問・議論を行う
2. 修正コミットを作成する（Step 3〜6 を再実施）
3. コミット後にレビュアーへ再レビューを依頼する

修正コミットの例:
```bash
git commit -m "fix(ktse): レビュー指摘対応 - null チェックを追加"
```

force push はレビュアーが確認中でない場合にのみ使用してよい。

### CIが失敗した場合

1. GitHub Actions のログを確認して原因を特定する
2. 修正コミットを作成してプッシュする
3. CIが通過するまで繰り返す

---

## Step 9: マージ

マージ条件がすべて揃ったことを確認してからマージを実施する。

| 条件 | 確認方法 |
|------|---------|
| 最低1名のレビュー承認 | GitHub PR の Approvals を確認 |
| CIが全て通過 | GitHub Actions のステータスを確認 |
| コンフリクトが解消済み | GitHub PR の Merge ボタンが有効かを確認 |

**マージ方法**: Squash Merge を使用する。

マージ後:
- 作業ブランチを削除する
- 関連するIssueがあればクローズする

---

## 特殊ケース

### 緊急修正（ホットフィックス）

本番環境の重大なバグ修正は `main` ブランチに対して直接PRを作成してよい。
ただし、速やかに `develop` ブランチにも反映すること。

### ドラフトPR

作業途中でフィードバックが欲しい場合はドラフトPRとして作成する:

```bash
gh pr create --base develop --draft ...
```

準備ができたら「Ready for review」に変更する。

---

## 参考

- [CONVENTION.md](../CONVENTION.md) — リポジトリ全体の規約
- [doc/pr-convention.md](pr-convention.md) — PR作成・レビュー規約
- [doc/ci-convention.md](ci-convention.md) — CI/CDワークフロー規約
- [doc/development-setup.md](development-setup.md) — 開発環境セットアップ
- [doc/glossary.md](glossary.md) — 用語集
