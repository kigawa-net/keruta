# デプロイ

## Dockerイメージのビルド

### ビルド

プロジェクトルートから以下のコマンドでDockerイメージをビルドします。

```bash
# プロジェクトルートで実行
docker build -f Dockerfile_ktcl_k8s -t harbor.kigawa.net/library/ktcl-k8s:latest .
```

### Harbor Registryへのプッシュ

```bash
# Harborにログイン
docker login harbor.kigawa.net

# イメージをプッシュ
docker push harbor.kigawa.net/library/ktcl-k8s:latest

# タグ付けしてバージョン管理
docker tag harbor.kigawa.net/library/ktcl-k8s:latest harbor.kigawa.net/library/ktcl-k8s:v1.0.0
docker push harbor.kigawa.net/library/ktcl-k8s:v1.0.0
```

### マルチアーキテクチャビルド（オプション）

```bash
# buildxでマルチプラットフォームビルド
docker buildx build --platform linux/amd64,linux/arm64 \
  -f Dockerfile_ktcl_k8s \
  -t harbor.kigawa.net/library/ktcl-k8s:latest \
  --push .
```

## ローカル開発

### kubeconfig認証を使用

```bash
# kubeconfig認証を使用
export K8S_USE_IN_CLUSTER="false"
export K8S_KUBECONFIG_PATH="~/.kube/config"

./gradlew :ktcl-k8s:run
```

## Kubernetes デプロイ

### 前提条件

- Kubernetes クラスタ（v1.24以上推奨）
- kubectl CLI
- RBAC権限

### RBAC設定

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ktcl-k8s
  namespace: default
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: ktcl-k8s
  namespace: default
rules:
- apiGroups: ["batch"]
  resources: ["jobs"]
  verbs: ["create", "get", "list", "watch", "delete"]
- apiGroups: ["batch"]
  resources: ["jobs/status"]
  verbs: ["get"]
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list", "watch"]
- apiGroups: [""]
  resources: ["pods/log"]
  verbs: ["get"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: ktcl-k8s
  namespace: default
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: Role
  name: ktcl-k8s
subjects:
- kind: ServiceAccount
  name: ktcl-k8s
  namespace: default
```

### Secret作成

```bash
# トークンをSecretとして作成
kubectl create secret generic keruta-tokens \
  --from-literal=user-token="YOUR_USER_TOKEN" \
  --from-literal=server-token="YOUR_SERVER_TOKEN" \
  -n default
```

### Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ktcl-k8s
  namespace: default
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
        image: harbor.kigawa.net/library/ktcl-k8s:latest
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
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

### デプロイ手順

```bash
# 1. RBACを適用
kubectl apply -f rbac.yaml

# 2. Secretを作成
kubectl apply -f secret.yaml

# 3. Deploymentを適用
kubectl apply -f deployment.yaml

# 4. 状態確認
kubectl get pods -l app=ktcl-k8s -n default
kubectl logs -f deployment/ktcl-k8s -n default
```

## GitHub Actions CI/CD

developブランチへのプッシュで自動デプロイが実行されます。

### ワークフロー

`.github/workflows/dev.yml`にktcl-k8sのビルド設定が含まれています：

```yaml
ktcl-k8s:
  uses: ./.github/workflows/release.yml
  secrets:
    HARBOR_PASS: ${{ secrets.HARBOR_PASS }}
    GIT_TOKEN: ${{ secrets.GIT_TOKEN }}
  with:
    name: ktcl-k8s
    manifestFile: ./keruta/dev/ktcl-k8s.yaml
    harborProject: "library"
    repository: "kigawa-net/kigawa-net-k8s"
    dockerFile: './Dockerfile_ktcl_k8s'
```

### 自動処理

1. Dockerイメージのビルド
2. Harbor Registry（`harbor.kigawa.net/library/ktcl-k8s`）へのプッシュ
3. `kigawa-net-k8s`リポジトリのマニフェストファイルのイメージタグ自動更新
4. ArgoCD経由での自動デプロイ（kigawa-net-k8sリポジトリ側で設定）

### 必要なSecrets

GitHubリポジトリに以下のSecretsを設定してください：

- `HARBOR_PASS`: Harbor Registryのパスワード
- `GIT_TOKEN`: kigawa-net-k8sリポジトリへのアクセストークン

## Webモードデプロイ

Webモードを有効化する場合は、追加でServiceを作成します：

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ktcl-k8s
  namespace: default
spec:
  type: ClusterIP
  ports:
  - name: http
    port: 8081
    targetPort: 8081
    protocol: TCP
  selector:
    app: ktcl-k8s
```

Ingress設定例：

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ktcl-k8s
  namespace: default
spec:
  rules:
  - host: ktcl-k8s.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: ktcl-k8s
            port:
              number: 8081
```