# Webモード

## 概要

Webモードでは、Keycloak OIDC認証を使用したWeb UIで設定管理が可能です。

## 起動

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

## OIDCログイン

Keycloakを使用したOpenID Connect認証により、安全なログインを実現します。

### ログインフロー

1. ブラウザでWeb UIにアクセス
2. 「Keycloakでログイン」ボタンをクリック
3. Keycloakログイン画面で認証
4. 認証成功後、設定管理画面にリダイレクト

### JWT検証

- Auth0 Java JWTライブラリを使用
- JWKS（JSON Web Key Set）を自動取得・キャッシング
- OIDC Discoveryで自動設定

## 設定管理

Web UIから以下の設定を変更可能です。

### Kubernetes設定

- **ネームスペース**: Job作成先のKubernetesネームスペース
- **In-Cluster認証の使用**: Pod内ServiceAccountを使用するか
- **Kubeconfigパス**: 外部kubeconfigファイルのパス
- **Jobタイムアウト**: Job実行のタイムアウト時間（秒）

### キュー設定

- **キューID**: 監視するタスクキューのID

### 反映タイミング

設定変更は即座にランタイムに反映されます（再起動不要）。

## API エンドポイント

Webモードでは以下のREST APIを提供します。

### 認証API

#### `POST /api/auth/login`

JWT トークンでログイン

**リクエスト**:
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**レスポンス**:
```json
{
  "success": true,
  "user": {
    "sub": "user-id",
    "email": "user@example.com",
    "name": "User Name"
  }
}
```

#### `POST /api/auth/logout`

ログアウト

**レスポンス**:
```json
{
  "success": true
}
```

#### `GET /api/auth/me`

現在のユーザー情報を取得

**レスポンス**:
```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "name": "User Name"
}
```

### 設定API

#### `GET /api/config`

現在の設定を取得

**レスポンス**:
```json
{
  "kubernetes": {
    "namespace": "default",
    "useInCluster": true,
    "kubeConfigPath": null,
    "jobTimeout": 600
  },
  "queue": {
    "queueId": 1
  }
}
```

#### `PUT /api/config/kubernetes`

Kubernetes設定を更新

**リクエスト**:
```json
{
  "namespace": "production",
  "useInCluster": true,
  "kubeConfigPath": null,
  "jobTimeout": 900
}
```

**レスポンス**:
```json
{
  "success": true,
  "config": {
    "namespace": "production",
    "useInCluster": true,
    "kubeConfigPath": null,
    "jobTimeout": 900
  }
}
```

#### `PUT /api/config/queue`

キュー設定を更新

**リクエスト**:
```json
{
  "queueId": 2
}
```

**レスポンス**:
```json
{
  "success": true,
  "config": {
    "queueId": 2
  }
}
```

## 認証とセキュリティ

### 認証が必要なエンドポイント

すべてのAPIエンドポイントは認証が必要です（`/api/auth/login`を除く）。

### セッション管理

- **セッション形式**: Ktor Sessions（Cookie-based）
- **セッション有効期限**: デフォルト1時間
- **セッションストレージ**: メモリ内（再起動でリセット）

### CORS設定

開発環境でのフロントエンド接続を許可するため、CORSが有効化されています。

```kotlin
install(CORS) {
    anyHost()
    allowCredentials = true
    allowHeader(HttpHeaders.ContentType)
}
```

## エラーレスポンス

すべてのエラーは以下の形式で返されます：

```json
{
  "error": "エラーメッセージ",
  "code": "ERROR_CODE"
}
```

### エラーコード例

- `UNAUTHORIZED`: 認証が必要
- `INVALID_TOKEN`: トークンが無効
- `CONFIG_ERROR`: 設定エラー
- `INTERNAL_ERROR`: 内部エラー