# Authentication Documentation

このドキュメントは、Kerutaの認証システムの詳細を説明します。

## 認証フロー（二重トークン検証）

KTSEサーバーは、ユーザートークンとプロバイダートークンの両方を検証する二重トークン認証を実装しています。

### 1. ユーザートークン検証

**プロセス:**
1. ユーザーのIdP（例: Auth0）が発行したJWTを受信
2. `Auth0JwtVerifier`でトークンをデコード・検証
3. JWK（JSON Web Key）をキャッシュ（LRUキャッシュ、最大8発行者）
4. OIDC Discovery経由でjwks_urlを自動取得
5. サブジェクト（subject）を抽出し、ユーザーを特定
6. `UserVerifier`: 初回認証時にユーザー作成、以降は既存ユーザーを再利用

**実装場所:**
- `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/UserVerifier.kt`
- `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/Auth0JwtVerifier.kt`

### 2. プロバイダートークン検証

**プロセス:**
1. プロバイダーのIdPが発行したJWTを検証
2. ユーザーの権限スコープに対してプロバイダートークンを検証
3. `ProviderVerifier`: プロバイダー情報をデータベースから取得・照合

**実装場所:**
- `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/ProviderVerifier.kt`

### 3. セッション確立

**プロセス:**
1. 両トークンが検証されると、`AuthenticatedPersisterSession`を作成
2. セッション状態を`MutableStateFlow`に保存
3. `KtcpSession`でセッションライフサイクルを管理

**セッションライフサイクル:**
```kotlin
// セッション開始
KtcpSession.startSession(connection, persisterSession)

// 認証リクエスト処理
persisterSession.verify(authRequestMsg) -> Res<AuthenticatedPersisterSession>

// 認証済みセッション保存
authenticatedSession.value = AuthenticatedSession(...)

// セッション使用
authenticatedSession.value?.createTask(task)
```

## セキュリティ設定

### JWT検証

- **アルゴリズム**: RSA256
- **JWKキャッシング**: LRUキャッシュ、最大8発行者
- **OIDC Discovery**: 自動jwks_url取得
- **トークン検証**: 署名、有効期限、issuer、audienceを検証

### セッション管理

- **タイムアウト**: 1分間タイムアウト
- **エラー許容**: 30分間で3エラーまで許容
- **自動切断**: エラーしきい値超過時

### CORS設定

- **開発環境**: `anyHost()`使用（全オリジン許可）
- **本番環境**: 特定オリジンのみ許可するよう変更を推奨

## IdP設定

### 設定ファイル（application.yaml）

```yaml
idp:
  issuers:
    - issuer: "https://example.auth0.com/"
      audience: "https://api.example.com"
      name: "Example Auth0"
```

### 環境変数

IdP設定は`application.yaml`から読み込まれますが、環境変数でオーバーライド可能です。

## 認証エントリーポイント

### ReceiveAuthRequestEntrypoint

**場所:** `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/ReceiveAuthRequestEntrypoint.kt`

**処理:**
1. クライアントからの認証リクエストを受信
2. `persisterSession.verify(authRequestMsg)`を呼び出し
3. 検証成功時、`AuthenticatedPersisterSession`を取得
4. セッション状態を更新
5. クライアントに認証成功レスポンスを送信

## エラーハンドリング

### 認証エラー

- **UnauthenticatedErr**: 未認証状態でアクセス試行
- **VerifyFailErr**: トークン検証失敗
- **VerifyUnsupportedKeyErr**: 未サポートの鍵タイプ

### エラーレスポンス

認証エラー時、クライアントに`GenericErrMsg`を送信：

```kotlin
when (val result = persisterSession.verify(authRequestMsg)) {
    is Res.Err -> {
        // エラーメッセージを送信
        sendGenericError(result.err)
    }
    is Res.Ok -> {
        // 認証成功処理
    }
}
```

## セキュリティベストプラクティス

1. **トークンの安全な保管**: クライアント側でトークンを安全に保管（LocalStorage避ける）
2. **HTTPS必須**: 本番環境では必ずHTTPSを使用
3. **短いトークン有効期限**: JWTの有効期限を短く設定
4. **リフレッシュトークン**: 長期セッション用にリフレッシュトークンを使用
5. **CORS設定厳格化**: 本番環境では特定オリジンのみ許可

## 未実装機能

現在、以下の機能は未実装です：

1. **リフレッシュトークン**: トークン更新メカニズム
2. **トークン失効**: ログアウト時のトークン無効化
3. **多要素認証（MFA）**: 追加の認証レイヤー