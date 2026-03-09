# ktcl-k8s - Keruta Kubernetes Client

KTCPプロトコル経由でタスクを受信し、Kubernetes Jobとして実行するクライアントモジュールです。

## 概要

ktcl-k8sは以下の機能を提供します：

1. **KTCPプロトコル統合**: KTSEサーバーとWebSocket経由で通信
2. **二重トークン認証**: ユーザートークン + プロバイダートークンで認証
3. **Kubernetes Job実行**: 受信したタスクをKubernetes Jobとして実行
4. **Job状態監視**: Job実行状態を監視し、タスクステータスを自動更新
5. **YAML定義ベース**: Job定義はYAMLファイルでカスタマイズ可能

## クイックスタート

### ビルド

```bash
./gradlew :ktcl-k8s:build
```

### CLIモード（デフォルト）

```bash
# 環境変数を設定
export KTSE_HOST="ktse.example.com"
export KTSE_PORT="8080"
export KERUTA_USER_TOKEN="user-token-here"
export KERUTA_SERVER_TOKEN="server-token-here"
export KERUTA_QUEUE_ID="1"
export K8S_NAMESPACE="default"
export K8S_USE_IN_CLUSTER="true"

# 実行
./gradlew :ktcl-k8s:run
```

### Webモード

```bash
# Webモードを有効化
export KTCL_K8S_WEB_MODE="true"
export KTCL_K8S_WEB_PORT="8081"

# Keycloak設定
export KEYCLOAK_URL="https://user.kigawa.net/"
export KEYCLOAK_REALM="develop"
export KEYCLOAK_CLIENT_ID="keruta"

# 実行
./gradlew :ktcl-k8s:run
```

Web UIにアクセス: `http://localhost:8081`

## ドキュメント

詳細な情報は以下のドキュメントを参照してください：

- **[アーキテクチャ](docs/architecture.md)** - システム構成、コンポーネント、動作フロー、技術スタック
- **[設定](docs/configuration.md)** - 環境変数、Job定義テンプレート、設定のカスタマイズ
- **[デプロイ](docs/deployment.md)** - Dockerビルド、Kubernetesデプロイ、GitHub Actions CI/CD
- **[Webモード](docs/web-mode.md)** - OIDCログイン、設定管理、API エンドポイント
- **[トラブルシューティング](docs/troubleshooting.md)** - よくある問題と対処法

## 主な環境変数

### 必須

- `KERUTA_USER_TOKEN` - ユーザー認証トークン
- `KERUTA_SERVER_TOKEN` - プロバイダー認証トークン
- `KERUTA_QUEUE_ID` - 監視するキューID

### オプション

- `KTSE_HOST` - KTSEサーバーホスト（デフォルト: localhost）
- `KTSE_PORT` - KTSEサーバーポート（デフォルト: 8080）
- `K8S_NAMESPACE` - Kubernetesネームスペース（デフォルト: default）
- `K8S_USE_IN_CLUSTER` - in-cluster認証使用（デフォルト: true）
- `KTCL_K8S_WEB_MODE` - Webモード有効化（デフォルト: false）

完全な環境変数リストは[設定ドキュメント](docs/configuration.md)を参照してください。

## 技術スタック

- **Kotlin Multiplatform**: JVM対応
- **Ktor Client**: WebSocket通信
- **Ktor Server**: Webモード
- **Kubernetes Java Client 25.x**: Kubernetes API統合
- **KTCP**: Kerutaプロトコル
- **Auth0 Java JWT**: JWT検証とJWKSサポート

## 参考

- [KTCP Protocol](../ktcp/README.md)
- [KTSE Server](../ktse/README.md)
- [Keruta Architecture](../doc/architecture.md)
