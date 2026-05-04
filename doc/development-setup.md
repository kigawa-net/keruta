# 開発環境セットアップガイド

kerutaプロジェクトの開発を始めるためのガイドです。

## 初回セットアップ

### Gitフックの設定

ブランチ名規約違反を防ぐため、Gitフックを設定します：

```bash
# プロジェクトルートで実行
chmod +x .git/hooks/pre-commit .git/hooks/pre-push
```

これで、コミットやプッシュ時に自動的にブランチ名がチェックされます。

## ブランチ命名規約

作業ブランチは必ず以下のプレフィックスで始めてください：

| プレフィックス | 用途 |
|---------------|------|
| `feature/` | 新機能開発 |
| `fix/` | バグ修正 |
| `fixes/` | 複数修正を含むブランチ |
| `doc/`, `docs/` | ドキュメント追加・更新 |
| `refactor/` | リファクタリング |
| `changes/` | 既存機能の変更 |
| `renovate/` | Renovateによる自動更新 |

**❌ 間違い例:**
- `features/some-feature` （`features/` ではなく `feature/`）
- `pr-123` （PR番号のみは不可）
- `my-work` （プレフィックスなし）

**✅ 正しい例:**
- `feature/kicl-web-user-settings`
- `fix/kicl-web-nav-on-index`
- `docs/update-readme`

## CIでの自動チェック

プルリクエスト作成時や `develop`/`main` へのプッシュ時に、CIが以下をチェックします：

1. **ブランチ名規約** - `.github/workflows/ci.yml` の `branch-naming-check` ジョブ
2. **ドキュメントファイル名** - `scripts/check-doc-filenames.sh`（ケバブケース）
3. **テスト** - `./gradlew test allTests`
4. **フロントエンドビルド** - ktcl-front, kicl-web

## 手動チェック

ローカルで手動チェックを行うこともできます：

```bash
# ブランチ名チェック
bash scripts/check-branch-naming.sh

# 現在のブランチのみチェック
bash scripts/check-branch-naming.sh $(git branch --show-current)

# ドキュメントファイル名チェック
bash scripts/check-doc-filenames.sh
```

## トラブルシューティング

### コミット時にブランチ名エラーが出る

ブランチ名を変更する必要があります：

```bash
# ブランチ名を変更
git branch -m <新しいブランチ名>
```

### プッシュ時にエラーが出る

`main` や `develop` への直接プッシュはできません。新しいブランチを作成してください。
