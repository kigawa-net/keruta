# ktcl-k8s - Keruta Kubernetes Client

KTCPプロトコル経由でタスクを受信し、Kubernetes Jobとして実行するクライアントモジュールです。

## 概要

ktcl-k8sは以下の機能を提供します：

1. **KTCPプロトコル統合**: KTSEサーバーとWebSocket経由で通信
2. **二重トークン認証**: ユーザートークン + プロバイダートークンで認証
3. **Kubernetes Job実行**: 受信したタスクをKubernetes Jobとして実行
4. **Job状態監視**: Job実行状態を監視し、タスクステータスを自動更新
5. **YAML定義ベース**: Job定義はYAMLファイルでカスタマイズ可能

## アーキテクチャ

```
┌──────────────┐     WebSocket/KTCP     ┌──────────────┐
│   KTSE       │<────────────────────────│  ktcl-k8s    │
│  (Server)    │                         │  (Client)    │
└──────────────┘                         └──────┬───────┘
                                               │
                                         Kubernetes API
                                               │
                                               ▼
                                       ┌──────────────┐
                                       │ Kubernetes   │
                                       │   Job        │
                                       └──────────────┘
```

## ビルドと実行

### ビルド

```bash
./gradlew :ktcl-k8s:build
```

### 実行モード

ktcl-k8sは、**CLIモード**と**Webモード**の2つの起動モードをサポートしています。

#### CLIモード（デフォルト）

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

#### Webモード

Webモードでは、Keycloak OIDC認証を使用したWeb UIで設定管理が可能です。

```bash
# Webモードを有効化
export KTCL_K8S_WEB_MODE="true"
export KTCL_K8S_WEB_PORT="8081"

# Keycloak設定
export KEYCLOAK_URL="https://user.kigawa.net/"
export KEYCLOAK_REALM="develop"
export KEYCLOAK_CLIENT_ID="keruta"

# 初期Kubernetes設定（オプション）
export K8S_NAMESPACE="default"
export K8S_USE_IN_CLUSTER="true"

# 実行
./gradlew :ktcl-k8s:run
```

Web UIにアクセス: `http://localhost:8081`

## 環境変数

### KTSE接続設定

| 環境変数 | デフォルト | 説明 |
|---------|----------|------|
| `KTSE_HOST` | localhost | KTSEサーバーホスト |
| `KTSE_PORT` | 8080 | KTSEサーバーポート |
| `KTSE_USE_TLS` | false | TLS使用 |
| `KERUTA_USER_TOKEN` | (必須) | ユーザー認証トークン |
| `KERUTA_SERVER_TOKEN` | (必須) | プロバイダー認証トークン |
| `KERUTA_QUEUE_ID` | (必須) | 監視するキューID |

### Kubernetes設定

| 環境変数 | デフォルト | 説明 |
|---------|----------|------|
| `K8S_NAMESPACE` | default | Kubernetesネームスペース |
| `K8S_JOB_TEMPLATE` | resources/job-template.yaml | Job定義YAMLパス |
| `K8S_USE_IN_CLUSTER` | true | in-cluster認証使用 |
| `K8S_KUBECONFIG_PATH` | ~/.kube/config | kubeconfigパス（in-cluster無効時） |
| `K8S_JOB_TIMEOUT` | 600 | Jobタイムアウト（秒） |

### Webモード設定

| 環境変数 | デフォルト | 説明 |
|---------|----------|------|
| `KTCL_K8S_WEB_MODE` | false | Webモードを有効化 |
| `KTCL_K8S_WEB_PORT` | 8081 | Webサーバーポート |
| `KEYCLOAK_URL` | https://user.kigawa.net/ | Keycloak URL |
| `KEYCLOAK_REALM` | develop | Keycloakレルム |
| `KEYCLOAK_CLIENT_ID` | keruta | KeycloakクライアントID |

## Job定義テンプレート

デフォルトのJob定義は`src/main/resources/job-template.yaml`に配置されています。

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: keruta-task-template
  namespace: default
spec:
  ttlSecondsAfterFinished: 3600  # 1時間後に自動削除
  backoffLimit: 0  # リトライなし
  template:
    spec:
      restartPolicy: Never
      containers:
      - name: task-executor
        image: harbor.kigawa.net/library/keruta-executor:latest
        env:
        - name: TASK_ID
          value: ""
        - name: TASK_TITLE
          value: ""
        - name: TASK_DESCRIPTION
          value: ""
```

### カスタマイズ

Job定義をカスタマイズする場合は、以下の環境変数を渡します：

- `TASK_ID`: タスクID
- `TASK_TITLE`: タスクタイトル
- `TASK_DESCRIPTION`: タスク説明

これらの環境変数はJobTemplateLoaderによって自動的に設定されます。

## 動作フロー

1. **起動**: WebSocket接続、KTCP認証
2. **タスク一覧取得**: 起動時に既存のpendingタスクを取得
3. **タスク受信**: ClientTaskListedMsg受信
4. **ステータス更新**: `status="running"`に更新
5. **Job作成**: Kubernetes BatchV1 APIでJob作成
6. **Job監視**: 5秒間隔でJob状態をポーリング
7. **ステータス更新**:
   - succeeded → `status="completed"`
   - failed/timeout → `status="failed"`

## デプロイ

### ローカル開発

```bash
# kubeconfig認証を使用
export K8S_USE_IN_CLUSTER="false"
export K8S_KUBECONFIG_PATH="~/.kube/config"

./gradlew :ktcl-k8s:run
```

### Kubernetes Pod内

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ktcl-k8s
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ktcl-k8s
  template:
    metadata:
      labels:
        app: ktcl-k8s
    spec:
      serviceAccountName: ktcl-k8s
      containers:
      - name: ktcl-k8s
        image: harbor.kigawa.net/library/keruta:latest
        env:
        - name: KTSE_HOST
          value: "ktse.default.svc.cluster.local"
        - name: KTSE_PORT
          value: "8080"
        - name: KERUTA_USER_TOKEN
          valueFrom:
            secretKeyRef:
              name: keruta-tokens
              key: user-token
        - name: KERUTA_SERVER_TOKEN
          valueFrom:
            secretKeyRef:
              name: keruta-tokens
              key: server-token
        - name: KERUTA_QUEUE_ID
          value: "1"
        - name: K8S_NAMESPACE
          value: "default"
        - name: K8S_USE_IN_CLUSTER
          value: "true"
```

### RBAC設定

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ktcl-k8s
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: ktcl-k8s
rules:
- apiGroups: ["batch"]
  resources: ["jobs"]
  verbs: ["create", "get", "list", "watch"]
- apiGroups: ["batch"]
  resources: ["jobs/status"]
  verbs: ["get"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: ktcl-k8s
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: ktcl-k8s
subjects:
- kind: ServiceAccount
  name: ktcl-k8s
```

## トラブルシューティング

### Job作成エラー

```bash
# Kubernetes APIへの接続を確認
kubectl cluster-info

# RBAC権限を確認
kubectl auth can-i create jobs --as=system:serviceaccount:default:ktcl-k8s
```

### Job監視エラー

```bash
# Job状態を確認
kubectl get jobs -n default
kubectl describe job keruta-task-<taskId> -n default
```

### 認証エラー

```bash
# トークンの有効性を確認
# KTSEサーバーのログを確認してください
```

## Webモード機能

### OIDCログイン

Keycloakを使用したOpenID Connect認証により、安全なログインを実現：

1. ブラウザでWeb UIにアクセス
2. 「Keycloakでログイン」ボタンをクリック
3. Keycloakログイン画面で認証
4. 認証成功後、設定管理画面にリダイレクト

### 設定管理

Web UIから以下の設定を変更可能：

#### Kubernetes設定
- ネームスペース
- In-Cluster認証の使用
- Kubeconfigパス
- Jobタイムアウト

#### キュー設定
- キューID

設定は即座にランタイムに反映されます（再起動不要）。

### API エンドポイント

Webモードでは以下のREST APIを提供：

- `POST /api/auth/login` - JWT トークンでログイン
- `POST /api/auth/logout` - ログアウト
- `GET /api/auth/me` - 現在のユーザー情報
- `GET /api/config` - 現在の設定を取得
- `PUT /api/config/kubernetes` - Kubernetes設定を更新
- `PUT /api/config/queue` - キュー設定を更新

すべてのAPIエンドポイントは認証が必要です（`/api/auth/login`を除く）。

## 技術スタック

- **Kotlin Multiplatform**: JVM対応
- **Ktor Client**: WebSocket通信
- **Ktor Server**: Webモード（認証、API、静的ファイル配信）
- **Kubernetes Java Client 25.x**: Kubernetes API統合
- **KTCP**: Kerutaプロトコル
- **kotlinx.serialization**: JSON処理
- **kotlinx.coroutines**: 非同期処理
- **Auth0 Java JWT**: JWT検証とJWKSサポート

## 参考

- [KTCP Protocol](../ktcp/README.md)
- [KTSE Server](../ktse/README.md)
- [Architecture Documentation](../doc/architecture.md)
