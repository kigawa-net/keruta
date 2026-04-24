# Authentication

## 認証フロー（二重トークン検証）

KTSEサーバーはユーザートークンとプロバイダートークンの両方を検証する二重トークン認証を実装。

### 1. ユーザートークン検証（OIDC）

1. ユーザーのIdP（例: Auth0）が発行したJWTを受信
2. `Auth0JwtVerifier` でデコード・検証
3. JWKをLRUキャッシュ（最大8発行者）
4. OIDC Discovery 経由で `jwks_url` を自動取得
5. サブジェクトを抽出しユーザーを特定
6. `UserVerifier`: 初回認証時にユーザー作成、以降は既存ユーザーを再利用

**実装:** `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/`

### 2. プロバイダートークン検証

1. プロバイダーのIdPが発行したJWTを検証
2. `ProviderVerifier`: プロバイダー情報をDBから取得・照合

### 3. セッション確立

両トークン検証後に `AuthenticatedPersisterSession` を作成。

```
KtcpSession.startSession()
  → PersisterSession.verify(authRequestMsg)
  → Res<AuthenticatedPersisterSession>
  → authenticatedSession.value = AuthenticatedSession(...)
```

## JWT検証設定

- アルゴリズム: RSA256
- JWKキャッシュ: LRUキャッシュ、最大8発行者
- OIDC Discovery: `jwks_url` 自動取得
- 検証項目: 署名、有効期限、issuer、audience

## IdP設定（application.yaml）

```yaml
idp:
  issuers:
    - issuer: "https://example.auth0.com/"
      audience: "https://api.example.com"
      name: "Example Auth0"
```

## セッション管理

- タイムアウト: 1分間無通信で切断
- エラー許容: 30分の時間窓で3エラーまで許容（超過で自動切断）

## 認証エントリーポイント（ReceiveAuthRequestEntrypoint）

1. クライアントから認証リクエスト受信
2. `persisterSession.verify(authRequestMsg)` 呼び出し
3. 成功 → セッション状態更新 → `AuthSuccessMsg` 送信
4. 失敗 → `GenericErrMsg` 送信（`VerifyFailErr` / `VerifyUnsupportedKeyErr`）

## 未実装機能

- リフレッシュトークン（トークン更新メカニズム）
- トークン失効（ログアウト時の無効化）
