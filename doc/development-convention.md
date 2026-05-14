# 開発手順規約

keruta プロジェクトにおける開発作業の標準手順を定める。
すべての開発者はIssue作成からPRマージまで、以下の手順に従うこと。

各フェーズの詳細ルールは関連規約を参照:
- [CONVENTION.md](../CONVENTION.md) — ブランチ・コミット・コードスタイル全般
- [doc/pr-convention.md](pr-convention.md) — PR作成・レビュー・マージ
- [doc/ci-convention.md](ci-convention.md) — CI/CDワークフロー

---

## 手順概要

```
Step 0: Issue作成
Step 1: 実装計画書作成・PRマージ
Step 2: 実装ブランチ作成
Step 3: 実装（レイヤー順）
Step 4: コードフォーマット
Step 5: テスト実行
Step 6: ビルド確認
Step 7: コミット
Step 8: PR作成
Step 9: レビュー対応
Step 10: マージ
```

手順はすべて順番通りに実施すること。スキップ・省略は禁止。

---

## Step 0: Issue作成

実装を開始する前に、必ず対応するIssueを作成する。

```bash
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
```

### ラベルの選択

| ラベル | 用途 |
|--------|------|
| `enhancement` | 新機能・機能拡張 |
| `bug` | バグ修正 |
| `documentation` | ドキュメント関連 |
| `refactoring` | リファクタリング |

**禁止事項**:
- 対応するIssueが存在しない状態で実装を開始すること
- Issueの概要・完了条件を空白にしたまま放置すること

作成したIssueの番号（`#NNN`）は後のStep 8（PR作成）で使用するため、必ず控えておくこと。

---

## Step 1: 実装計画書作成・PRマージ

実装を開始する前に、必ず実装計画書を作成し、PRをマージすること。

```bash
git checkout develop && git pull origin develop
git checkout -b docs/{module}-{feature}-plan
```

`doc/plan/{module}-{feature}.md` に以下の内容を記述する:

```markdown
# {機能名} 実装計画

## 実装方針
何をどのように実装するかの概要

## ファイル・クラス構成
- 追加/変更するファイル一覧

## 実装順序
1. domain層: ...
2. usecase層: ...
3. infra層: ...

## テスト方針
- テストするシナリオ
```

```bash
git add doc/plan/{module}-{feature}.md
git commit -m "docs({module}): {feature}の実装計画を追加"

gh pr create --base develop \
  --title "docs({module}): {feature}の実装計画" \
  --body "$(cat <<'EOF'
## 実装方針
...

## 主な変更点
- 追加/変更するファイル

## 関連
- Issue: #番号
EOF
)"
```

**計画PRがマージされるまで実装ブランチを作成してはならない。**

---

## Step 2: 実装ブランチ作成

計画PRのマージ後に実装ブランチを作成する。

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

## Step 3: 実装

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

## Step 4: コードフォーマット

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

## Step 5: テスト実行

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

## Step 6: ビルド確認

```bash
./gradlew :{module}:build
```

ビルドが失敗している状態でコミットしてはならない。

---

## Step 7: コミット

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

## Step 8: PR作成

### 事前チェック

- [ ] ベースブランチが `develop` であること
- [ ] CIが全て通過していること
- [ ] 1 PR = 1機能（または1修正）の粒度であること
- [ ] `doc/pr-convention.md` を確認済みであること
- [ ] Step 0 で作成したIssue番号を手元に用意していること

### PR作成コマンド

```bash
# <Issue番号> は Step 0 で作成したIssueの番号に置き換える
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
- Closes #<Issue番号>
EOF
)"
```

**重要な注意事項**:
- `--base develop` の省略は禁止。省略した場合、デフォルトブランチへのPRが誤って作成される
- `Closes #<Issue番号>` を必ず記載すること。PRマージ時にIssueが自動クローズされる
- Issue番号が不明な場合は `gh issue list --state open` で確認すること

---

## Step 9: レビュー対応

### レビュー指摘への対応

1. 指摘内容を理解し、必要であれば質問・議論を行う
2. 修正コミットを作成する（Step 4〜7 を再実施）
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

## Step 10: マージ

マージ条件がすべて揃ったことを確認してからマージを実施する。

| 条件 | 確認方法 |
|------|---------|
| 最低1名のレビュー承認 | GitHub PR の Approvals を確認 |
| CIが全て通過 | GitHub Actions のステータスを確認 |
| コンフリクトが解消済み | GitHub PR の Merge ボタンが有効かを確認 |

**マージ方法**: Squash Merge を使用する。

マージ後:
- 作業ブランチを削除する
- `Closes #NNN` を記載していれば、マージと同時にIssueが自動クローズされる
- 自動クローズされなかった場合は手動でIssueをクローズする: `gh issue close <Issue番号>`

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
