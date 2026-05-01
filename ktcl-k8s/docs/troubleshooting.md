# トラブルシューティング

## Job作成エラー

### 症状

```
K8sClientErr: Failed to create job
```

### 原因と対処法

#### 1. Kubernetes APIへの接続エラー

**確認方法**:
```bash
# Kubernetes APIへの接続を確認
kubectl cluster-info
```

**対処法**:
- `K8S_USE_IN_CLUSTER=true`の場合: ServiceAccountのトークンが正しくマウントされているか確認
- `K8S_USE_IN_CLUSTER=false`の場合: kubeconfigファイルのパスと内容を確認

#### 2. RBAC権限エラー

**確認方法**:
```bash
# RBAC権限を確認
kubectl auth can-i create jobs --as=system:serviceaccount:default:ktcl-k8s
kubectl auth can-i get jobs --as=system:serviceaccount:default:ktcl-k8s
```

**対処法**:
- ServiceAccount、Role、RoleBindingが正しく作成されているか確認
- Roleに必要な権限（`create`, `get`, `list`, `watch`）が含まれているか確認

```bash
# RBACリソースを確認
kubectl get serviceaccount ktcl-k8s -n default
kubectl get role ktcl-k8s -n default -o yaml
kubectl get rolebinding ktcl-k8s -n default -o yaml
```

#### 3. ネームスペースエラー

**確認方法**:
```bash
# ネームスペースが存在するか確認
kubectl get namespace default
```

**対処法**:
- `K8S_NAMESPACE`環境変数が正しく設定されているか確認
- 指定したネームスペースが存在するか確認

## Job監視エラー

### 症状

```
JobWatchErr: Failed to watch job status
```

### 原因と対処法

#### 1. Job状態の確認

**確認方法**:
```bash
# Job状態を確認
kubectl get jobs -n default
kubectl describe job keruta-task-<taskId> -n default

# Podログを確認
kubectl logs -l job-name=keruta-task-<taskId> -n default
```

**対処法**:
- Jobが正常に作成されているか確認
- Podがスケジュールされているか確認
- Podがイメージをプルできているか確認

#### 2. Job タイムアウト

**確認方法**:
```bash
# Jobの実行時間を確認
kubectl get job keruta-task-<taskId> -n default -o yaml | grep startTime
```

**対処法**:
- `K8S_JOB_TIMEOUT`環境変数を増やす（デフォルト600秒）
- Job定義テンプレートの`activeDeadlineSeconds`を調整

## 認証エラー

### 症状

```
Authentication failed: UnauthenticatedErr
```

### 原因と対処法

#### 1. トークンの有効性

**確認方法**:
```bash
# 環境変数を確認
echo $KERUTA_USER_TOKEN
echo $KERUTA_SERVER_TOKEN
```

**対処法**:
- トークンが正しく設定されているか確認
- トークンの有効期限が切れていないか確認（JWTの場合）
- KTSEサーバーのログを確認してトークン検証エラーの詳細を確認

#### 2. KTSE接続エラー

**確認方法**:
```bash
# KTSEサーバーへの接続を確認
curl -v ws://$KTSE_HOST:$KTSE_PORT/ws/ktcp
```

**対処法**:
- `KTSE_HOST`と`KTSE_PORT`が正しく設定されているか確認
- KTSEサーバーが起動しているか確認
- ネットワーク接続（ファイアウォール、DNS）を確認

## テンプレート読み込みエラー

### 症状

```
TemplateLoadErr: Failed to load job template
```

### 原因と対処法

#### 1. テンプレートファイルの存在確認

**確認方法**:
```bash
# テンプレートファイルを確認
ls -la src/main/resources/job-template.yaml
```

**対処法**:
- `K8S_JOB_TEMPLATE`環境変数が正しいパスを指しているか確認
- ファイルが存在し、読み取り権限があるか確認

#### 2. テンプレートファイルの形式

**対処法**:
- YAMLファイルの構文が正しいか確認
- 必須フィールド（`apiVersion`, `kind`, `metadata`, `spec`）が含まれているか確認

```bash
# YAML構文チェック
kubectl apply --dry-run=client -f job-template.yaml
```

## Webモードエラー

### 症状

```
Failed to start web server
```

### 原因と対処法

#### 1. ポート競合

**確認方法**:
```bash
# ポートが使用中か確認
lsof -i :8081
netstat -tuln | grep 8081
```

**対処法**:
- `KTCL_K8S_WEB_PORT`環境変数を別のポートに変更
- 既存のプロセスを停止

#### 2. Keycloak設定エラー

**確認方法**:
```bash
# Keycloak設定を確認
echo $KEYCLOAK_URL
echo $KEYCLOAK_REALM
echo $KEYCLOAK_CLIENT_ID
```

**対処法**:
- Keycloak URLが正しいか確認（末尾のスラッシュに注意）
- レルムとクライアントIDが存在するか確認
- OIDC Discovery URLにアクセスできるか確認

```bash
# OIDC Discovery エンドポイントを確認
curl $KEYCLOAK_URL/realms/$KEYCLOAK_REALM/.well-known/openid-configuration
```

## デバッグログの有効化

### ログレベルの変更

アプリケーションログレベルを変更してデバッグ情報を出力します。

```bash
# JVMシステムプロパティでログレベルを設定
./gradlew :ktcl-k8s:run -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

### Kubernetes Clientのデバッグログ

```kotlin
// K8sClientFactory.kt内で設定
val config = ConfigBuilder()
    .withNamespace(k8sConfig.k8sNamespace)
    .withLoggingLevel(Level.ALL)  // すべてのログを出力
    .build()
```

## よくある問題

### Job が永遠に Running 状態

**原因**: Jobテンプレートの`restartPolicy`が`Always`に設定されている

**対処法**: `restartPolicy`を`Never`または`OnFailure`に変更

### Job が即座に Failed

**原因**: コンテナイメージが存在しない、または起動エラー

**対処法**:
```bash
# Podログを確認
kubectl logs -l job-name=keruta-task-<taskId> -n default

# イベントを確認
kubectl describe pod -l job-name=keruta-task-<taskId> -n default
```

### メモリ不足エラー

**原因**: JVMヒープサイズが不足

**対処法**:
```bash
# JVMヒープサイズを増やす
export JAVA_OPTS="-Xmx2g"
./gradlew :ktcl-k8s:run
```

Kubernetes Deployment の場合:
```yaml
resources:
  limits:
    memory: "2Gi"
```