# Keruta Development Template for Coder

このテンプレートは、Kerutaプロジェクトの開発環境をCoderで構築するためのものです。

## 概要

このテンプレートは以下の開発環境を提供します：

- **Java 21** - Spring Boot アプリケーション開発
- **Go 1.21** - Keruta Agent 開発
- **Node.js 20** - Keruta Admin フロントエンド開発
- **Docker** - コンテナ化とサービス管理
- **Kubernetes CLI** - K8s連携機能の開発
- **MongoDB Tools** - データベース管理
- **Code Server** - ブラウザベースのIDE

## 含まれるツール

### 開発ツール
- OpenJDK 21 (Spring Boot開発用)
- Go 1.21.5 (Agent開発用)
- Node.js 20 (Admin UI開発用)
- Docker & Docker Compose
- kubectl
- MongoDB Shell & Database Tools

### エディタ拡張機能
- Java Extension Pack
- Go Extension
- Kubernetes Tools
- MongoDB Extension
- TypeScript/JavaScript サポート
- YAML/JSON サポート

## 使用方法

### 1. Dockerイメージのビルド

```bash
cd .coder-templates/keruta-dev
./build.sh
```

イメージをレジストリにプッシュする場合：

```bash
./build.sh push
```

### 2. Coderテンプレートの作成

1. Coder管理画面にアクセス
2. 「Templates」→「Create Template」
3. `main.tf` ファイルをアップロード
4. 必要に応じて変数を設定

### 3. ワークスペースの作成

1. テンプレートから新しいワークスペースを作成
2. 必要なリソース設定を行う
3. ワークスペースを起動

## 設定可能な変数

| 変数名 | 説明 | デフォルト値 |
|--------|------|-------------|
| `namespace` | Kubernetesネームスペース | `coder-workspaces` |
| `storage_class` | ストレージクラス | `local-path` |
| `cpu_limit` | CPU制限 | `2` |
| `memory_limit` | メモリ制限 | `4Gi` |
| `cpu_request` | CPU要求 | `500m` |
| `memory_request` | メモリ要求 | `1Gi` |
| `home_disk_size` | ホームディスクサイズ (GB) | `10` |

## 利用可能なアプリケーション

### Code Server
- URL: `http://localhost:8080`
- 機能: ブラウザベースのVS Code環境

### Keruta API
- URL: `http://localhost:8080/api`
- 機能: REST API エンドポイント

### Keruta Admin
- URL: `http://localhost:8080/admin`
- 機能: 管理パネルUI

## 開発ワークフロー

### 1. 環境セットアップ

ワークスペース起動時に自動的に以下が実行されます：

- Kerutaリポジトリのクローン
- 開発環境の設定
- MongoDB の起動
- プロジェクトのビルド

### 2. 開発コマンド

```bash
# プロジェクトビルド
./gradlew build

# API サーバー起動
./gradlew :api:bootRun

# テスト実行
./gradlew test

# コードフォーマット
./gradlew ktlintFormatAll

# MongoDB 起動
docker-compose up -d mongodb

# Go Agent ビルド
cd keruta-agent && ./scripts/build.sh
```

### 3. 便利なエイリアス

以下のエイリアスが自動的に設定されます：

- `k` → `kubectl`
- `d` → `docker`
- `dc` → `docker-compose`
- `g` → `git`

## トラブルシューティング

### Docker権限エラー
```bash
sudo usermod -aG docker coder
```

### MongoDB接続エラー
```bash
docker-compose up -d mongodb
# または
docker run -d --name keruta-mongodb -p 27017:27017 mongo:latest
```

### ポート競合
デフォルトでは8080ポートを使用します。競合する場合は、`main.tf`の設定を変更してください。

## 要件

### Kubernetes クラスタ
- Kubernetes 1.20+
- 設定可能なストレージクラス
- Docker Socket アクセス（オプション）

### リソース要件
- CPU: 最小 500m、推奨 2 cores
- メモリ: 最小 1Gi、推奨 4Gi
- ストレージ: 最小 10Gi

## カスタマイズ

### 追加パッケージの install
`Dockerfile` を編集して必要なパッケージを追加できます。

### 追加の VS Code 拡張機能
`Dockerfile` の拡張機能インストール部分を編集します。

### 環境変数の設定
`main.tf` の environment 設定を変更します。

## 注意事項

- このテンプレートは開発環境用です。本番環境では使用しないでください。
- Docker Socket のマウントはセキュリティリスクがあります。本番環境では適切な権限設定を行ってください。
- リソース制限は環境に応じて調整してください。

## サポート

問題が発生した場合は、以下を確認してください：

1. Kubernetes クラスタの状態
2. Docker イメージの利用可能性
3. ストレージクラスの設定
4. ネットワーク設定

詳細なログはワークスペースのコンテナログを確認してください。