# 設定

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

### テンプレート例

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

### カスタマイズ可能な環境変数

Job定義をカスタマイズする場合は、以下の環境変数を使用できます：

- `TASK_ID`: タスクID
- `TASK_TITLE`: タスクタイトル
- `TASK_DESCRIPTION`: タスク説明

これらの環境変数はJobTemplateLoaderによって自動的に設定されます。

### テンプレートのカスタマイズ例

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: keruta-task-template
  namespace: default
  labels:
    app: keruta-executor
    managed-by: ktcl-k8s
spec:
  ttlSecondsAfterFinished: 7200  # 2時間後に自動削除
  backoffLimit: 3  # 3回までリトライ
  template:
    spec:
      restartPolicy: OnFailure
      containers:
      - name: task-executor
        image: harbor.kigawa.net/library/keruta-executor:latest
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        env:
        - name: TASK_ID
          value: ""
        - name: TASK_TITLE
          value: ""
        - name: TASK_DESCRIPTION
          value: ""
        - name: LOG_LEVEL
          value: "INFO"
```

## 設定のプライオリティ

1. **環境変数**: 最優先（起動時に読み込み）
2. **システムプロパティ**: `-DK8S_NAMESPACE=production`形式
3. **デフォルト値**: K8sConfig内で定義

## 設定の検証

起動時に以下の設定を検証します：

- `KERUTA_USER_TOKEN`が設定されているか
- `KERUTA_SERVER_TOKEN`が設定されているか
- `K8S_JOB_TEMPLATE`ファイルが存在するか（CLIモード時）
- Kubernetes Clientが初期化できるか