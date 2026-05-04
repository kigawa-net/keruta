# Authentication

## 認証フロー概要

kerutaプロジェクトでは、2つの異なる認証フローを実装しています：
- **KTSEサーバー**: 二重トークン認証（ユーザー+プロバイダー）
- **ktcl-k8sサーバー**: OIDC + PKCE認証フロー（OpenID Connect）

---

## 1. KTSEサーバー（二重トークン認証）

KTSEサーバーはユーザートークンとプロバイダートークンの両方を検証する二重トークン認証を実装。

### 1.1 ユーザートークン検証（OIDC）

1. ユーザーのIdP（例: Auth0）が発行したJWTを受信
2. `Auth0JwtVerifier` でデコード・検証
3. JWKをLRUキャッシュ（最大8発行者）
4. OIDC Discovery 経由で `jwks_url` を自動取得
5. サブジェクトを抽出しユーザーを特定
6. `UserVerifier`: 初回認証時にユーザー作成、以降は既存ユーザーを再利用

**実装:** `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/`

### 1.2 プロバイダートークン検証

1. プロバイダーのIdPが発行したJWTを検証
2. `ProviderVerifier`: プロバイダー情報をDBから取得・照合

### 1.3 セッション確立

両トークン検証後に `AuthenticatedPersisterSession` を作成。

```
KtcpSession.startSession()
  → PersisterSession.verify(authRequestMsg)
  → Res<AuthenticatedPersisterSession>
  → authenticatedSession.value = AuthenticatedSession(...)
```

---

## 2. ktcl-k8sサーバー（OIDC + PKCEフロー）

ktcl-k8sモジュールでは、Webブラウザ経由のOIDC認証にPKCE（Proof Key for Code Exchange）を組み合わせたフローを実装。

### 2.1 認証フロー全体図

```
[ユーザー] → [ktcl-k8s /login] → [IdP (Keycloak等)]
     ↑                                      ↓
     └────────── [callback /login/callback] → [トークン交換] → [セッション作成]
```

### 2.2 詳細ステップ

#### Step 1: ログイン開始 (`/login`)
- **エンドポイント**: `LoginRoute.kt`
- **処理内容**:
  1. PKCEパラメータ生成:
     - `code_verifier`: ランダム文字列（43-128文字）
     - `code_challenge`: verifierをSHA256ハッシュし、Base64URLエンコード
  2. 認証サーバーへリダイレクト:
     ```
     GET {issuer}/protocol/openid-connect/auth?
         client_id={client_id}&
         response_type=code&
         scope=openid profile email&
         redirect_uri={callback_url}&
         state={random_state}&
         code_challenge={challenge}&
         code_challenge_method=S256
     ```

#### Step 2: IdPでの認証
- ユーザーがIdP（Keycloak等）のログインフォームで認証情報を入力

#### Step 3: コールバック処理 (`/login/callback`)
- **エンドポイント**: `LoginCallbackRoute.kt`
- **処理内容**:
  1. `state` パラメータ検証（CSRF防止）
  2. 認証コードを使ってトークンリクエスト:
     ```
     POST {issuer}/protocol/openid-connect/token
         grant_type=authorization_code
         code={認可コード}
         redirect_uri={callback_url}
         client_id={client_id}
         code_verifier={code_verifier}
     ```
  3. レスポンスで受け取った **IDトークン** を検証 (`JwtVerifier.kt`):
     - 署名検証
     - 発行者(issuer)検証
     - 対象者(audience)検証
     - 有効期限検証
  4. IDトークンから `userSubject`、`userIssuer`、`userAudience` を抽出
  5. ユーザー特定/作成 (`UserDao.findOrCreate()`) - **Exposed 1.x対応済み**
  6. リフレッシュトークンをDBに保存 (`UserTokenDao.saveOrUpdate()`) - **Exposed 1.x対応済み**
  7. セッション作成 (`OidcSession`) し、セッションCookieを設定

#### Step 4: 以降のリクエスト
- セッションCookieが送信され、`AuthGuard` 等で認証状態をチェック
- アクセストークンが期限切れの場合:
  - `TokenRefresher.kt` がリフレッシュトークンを使って新しいアクセストークンを取得

### 2.3 主要ファイルと役割

| ファイル | 役割 |
|--------|------|
| `ktcl-k8s/.../login/LoginRoute.kt` | `/login` エンドポイント。PKCEパラメータ生成と認証サーバーへのリダイレクト |
| `ktcl-k8s/.../login/LoginCallbackRoute.kt` | `/login/callback` エンドポイント。認証サーバーからのコールバック処理 |
| `ktcl-k8s/.../login/TokenRoute.kt` | `/token` エンドポイント。トークンリフレッシュ処理 |
| `ktcl-k8s/.../auth/TokenRefresher.kt` | リフレッシュトークンを使ったアクセストークン更新 |
| `ktcl-k8s/.../auth/JwtVerifier.kt` | IDトークンの検証（署名、発行者、有効期限等） |
| `ktcl-k8s/.../persist/dao/UserDao.kt` | ユーザー情報のDB検索/作成（Exposed 1.x対応済み） |
| `ktcl-k8s/.../persist/dao/UserTokenDao.kt` | リフレッシュトークンのDB保存/取得（Exposed 1.x対応済み） |
| `ktcl-k8s/.../config/IdpConfig.kt` | 認証サーバー（IdP）の設定情報 |
| `ktcl-k8s/.../login/OidcSession.kt` | セッション管理（ログイン状態保持） |

### 2.4 PKCE (Proof Key for Code Exchange)

- **目的**: 認可コードインターセプション攻撃を防ぐためのセキュリティ強化
- **code_verifier**: 43-128文字のランダム文字列
- **code_challenge**: `BASE64URL(SHA256(verifier))`
- **検証**: トークンリクエスト時に `code_verifier` を送信し、認証サーバーが `challenge` と一致するか検証

### 2.5 設定例（application.yaml）

```yaml
idp:
  issuer: "https://keycloak.example.com/realms/myrealm"
  clientId: "ktcl-k8s-client"
  clientSecret: "${IDP_CLIENT_SECRET}"
  redirectUri: "https://ktcl-k8s.example.com/login/callback"
```

---

## JWT検証設定（共通）

- アルゴリズム: RSA256
- JWKキャッシュ: LRUキャッシュ、最大8発行者
- OIDC Discovery: `jwks_url` 自動取得
- 検証項目: 署名、有効期限、issuer、audience

---

## セッション管理

### KTSE
- タイムアウト: 1分間無通信で切断
- エラー許容: 30分の時間窓で3エラーまで許容（超過で自動切断）

### ktcl-k8s
- セッション管理方式: `OidcSession` + Cookie
- リフレッシュトークンによる自動更新

---

## 認証エントリーポイント

### KTSE（ReceiveAuthRequestEntrypoint）
1. クライアントから認証リクエスト受信
2. `persisterSession.verify(authRequestMsg)` 呼び出し
3. 成功 → セッション状態更新 → `AuthSuccessMsg` 送信
4. 失敗 → `GenericErrMsg` 送信（`VerifyFailErr` / `VerifyUnsupportedKeyErr`）

### ktcl-k8s（HTTPエンドポイント）
1. `GET /login` → PKCEパラメータ生成 → IdPへリダイレクト
2. `GET /login/callback` → コールバック処理 → セッション作成
3. `POST /token` → リフレッシュトークンでアクセストークン更新

---

## 未実装機能

- [ ] トークン失効（ログアウト時の無効化）
- [ ] 広範なログアウト（すべてのセッションを無効化）
- [x] Exposed 1.x マイグレーション完了（ktcl-k8s, ktse）
