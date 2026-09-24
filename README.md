# Keruta プロジェクト

Kotlin Multiplatform を活用したタスク管理システム。WebSocketプロトコル(KTCP)によるリアルタイム通信、Kubernetes Job実行、OIDC認証などの機能を提供。

## 🚀 クイックスタート

### 初期セットアップ

```bash
# リポジトリをクローン
git clone https://github.com/kigawa-net/keruta.git
cd keruta

# Git hooks をセットアップ（規約チェックを自動化）
cp scripts/hooks/pre-commit.template .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
cp scripts/hooks/pre-push.template .git/hooks/pre-push
chmod +x .git/hooks/pre-push

# 開発環境を構築
./gradlew build
```

### 開発サーバー起動

```bash
# タスクサーバー (ktse)
./gradlew :ktse:run

# K8s クライアント (ktcl-k8s)
./gradlew :ktcl-k8s:run

# フロントエンド (ktcl-front)
cd ktcl-front && npm run dev
```

## 📋 規約とドキュメント

このプロジェクトでは、以下の規約を遵守する必要があります。**必ずお読みください。**

| ドキュメント | 説明 |
|-------------|------|
| [CONVENTION.md](CONVENTION.md) | **リポジトリ全体の規約**（ブランチ戦略、コミットメッセージ、PR作成ルール等） |
| [doc/pr-convention.md](doc/pr-convention.md) | Pull Request 作成・運用規約 |
| [doc/ci-convention.md](doc/ci-convention.md) | CI/CD ワークフロー作成・運用規約 |
| [doc/development-setup.md](doc/development-setup.md) | 開発環境セットアップ詳細 |
| [doc/glossary.md](doc/glossary.md) | プロジェクト用語集 |

### 規約を守るための仕組み

1. **ブランチ名チェックスクリプト**
   ```bash
   # 手動でブランチ名をチェック
   scripts/check-branch-naming.sh [ブランチ名]
   ```

2. **Git Hooks（自動チェック）**
   - `pre-commit`: コミット時にブランチ名をチェック
   - `pre-push`: プッシュ時にブランチ名とmain/developへの直接プッシュをチェック

3. **CI/CD 自動チェック**
   - ブランチ名規約の自動チェック
   - ktlintCheck、テスト実行
   - PR テンプレートに基づくチェック

## 🛠 開発コマンド

### ビルド

```bash
./gradlew build                          # 全モジュールビルド
./gradlew :ktse:build                    # 個別モジュール
```

### テスト

```bash
./gradlew test                           # 全テスト実行
./gradlew test --tests "*ReceiveUnknownArgTest"  # クラス名指定
./gradlew test --continue                # 失敗しても続行
```

### コードスタイル・リント

```bash
./gradlew ktlintFormat                   # 自動フォーマット
./gradlew ktlintCheck                    # リントチェック
```

### データベース

```bash
docker-compose -f compose.test.yml up -d mysql   # テスト用MySQL起動
```

## 📦 モジュール構成

| モジュール | 説明 |
|-----------|------|
| `kodel` | 共通ライブラリ（Res型、EntrypointDeferred、Kogger） |
| `ktcp` | WebSocketプロトコル（Kotlin Multiplatform対応） |
| `ktse` | Ktorタスクサーバー（Exposed/Flyway/MySQL、二重トークン認証） |
| `ktcl-k8s` | KTCPでタスク受信しKubernetes Jobとして実行 |
| `ktcl-front` | React+TypeScript+Vite+Keycloak.js（既存フロントエンド） |
| `kicl-web` | React Router v7 + Kotlin Multiplatform共有ロジック（次世代フロントエンド） |
| `kicl` | Kotlin Multiplatformモジュール（domain/usecase） |
| `kicp` | クロスドメインIDフェデレーションプロトコル |
| `ktcl-claudecode` | Claude Code統合 |

詳細は各モジュールのドキュメントや `doc/` ディレクトリを参照してください。

## 🌐 デプロイ

```bash
# Dockerイメージのビルド
docker build -f Dockerfile_ktse -t harbor.kigawa.net/library/ktse:latest .
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .
docker build -f Dockerfile_ktcl_front -t harbor.kigawa.net/private/ktcl-front:latest .
```

- `develop` ブランチへの push で自動デプロイ（dev環境）
- 詳細は [CONVENTION.md](CONVENTION.md#8-デプロイフロー) を参照

## 🤝 貢献方法

1. このリポジトリをフォーク/クローン
2. **規約を遵守**: [CONVENTION.md](CONVENTION.md) を必ずお読みください
3. 作業ブランチを作成: `git checkout -b feature/your-feature`
4. 変更を実装・テスト
5. コミットメッセージは [Conventional Commits](https://www.conventionalcommits.org/) 形式で記述
6. プッシュ前にチェック: `./gradlew ktlintCheck test`
7. Pull Request を `develop` ブランチへ作成（[PRテンプレート](.github/pull_request_template.md)使用）

## 📖 参考リンク

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Ktor Framework](https://ktor.io/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [GitHub Flow](https://docs.github.com/ja/get-started/quickstart/github-flow)

## 📄 ライセンス

（必要に応じてライセンスを記載）
