# KICP: クロスドメインIDフェデレーションプロトコル

## 概要

KICP (Kigawa Identity Cross-domain Protocol) は、異なるドメインの idServer 間でユーザーIDをフェデレーションするプロトコル。OIDCトークンとプロバイダートークンの両方を検証し、登録トークンを使ってクロスドメイン登録を行う。

**kiseサーバーがこのプロトコルを実装し、kicl-webはkiseを通じてkicpを使用する。**

---

## 1. フロー全体図

### ログインフロー（単一ドメイン）

```
clientA ──ログイン──► oidcA          → OIDCアクセストークン取得
clientA ──取得────► providerA        → プロバイダートークン取得
clientA ──ログイン──► idServerA      (oidcToken + providerToken)
  idServerA: providerToken を providerA の JWKS で検証
  idServerA: oidcToken を oidcA の JWKS で検証
idServerA ──► clientA                → IdentityId 返却（認証済み）
```

### クロスドメイン登録フロー（idServerA ↔ idServerB）

```
clientA ──登録トークン取得──► idServerA  → RegisterToken（5分有効）
clientA ──リダイレクト──► clientB        (registerToken を渡す)

clientB ──ログイン──► oidcB              → OIDCアクセストークン取得
clientB ──取得────► providerB            → プロバイダートークン取得
clientB ──登録──► idServerB             (oidcToken + providerToken + registerToken)
  idServerB: providerToken + oidcToken を検証
  idServerB: RegisterId を生成（oidcB の issuer:subject）
  idServerB ──verify──► idServerA        (registerId + registerToken)
    idServerA: registerToken をDBで検索
    idServerA: 有効期限を確認
    idServerA: registerToken を削除（リプレイ防止）
    idServerA ──► idServerB              → success（creatorIdentityId）
idServerB ──► clientB                   → IdentityId 返却（認証済み）
```

---

## 2. ドメイン型

### 識別子

| 型 | 形式 | 説明 |
|---|---|---|
| `IdentityId` | `"{issuer}:{subject}"` | idServer 内のユーザー識別子 |
| `RegisterId` | `"{issuer}:{subject}"` | 登録クライアント（idServerB 側ユーザー）の識別子 |

```kotlin
data class IdentityId(val value: String)  // 例: "https://accounts.google.com:abc123"
data class RegisterId(val value: String)  // 例: "https://auth.example.com:xyz789"
```

`IdentityId` と `RegisterId` は値の構造が同じだが、**意味が異なる**：
- `IdentityId`: **認証済みユーザー**を表す（idServerA 側）
- `RegisterId`: **登録しようとしているユーザー**を表す（idServerB 側）

### トークン

| 型 | 説明 | 有効期限 |
|---|---|---|
| `OidcToken` | OIDC仕様のJWT（IdPが発行） | IdPの設定による |
| `ProviderToken` | プロバイダーが署名したJWT | プロバイダーの設定による |
| `RegisterToken` | クロスドメイン登録用一時トークン | デフォルト5分・1回限り |

```kotlin
data class OidcToken(val value: String)
data class ProviderToken(val value: String)
data class RegisterToken(val value: String)
```

### TokenClaims（JWT検証後の抽出値）

JWTを検証した結果として得られるクレーム情報：

```kotlin
@Serializable
data class TokenClaims(
    val issuer: String,         // トークン発行者のURL
    val subject: String,        // ユーザー識別子（OIDCのsub）
    val audience: List<String>, // 対象受信者
)
```

### JWKS（JSON Web Key Set）

```kotlin
data class JwksUrl(val value: String)          // JWKSエンドポイントのURL
data class Jwks(val keys: List<JwkKey>)        // JWKSコンテナ

@Serializable
data class JwkKey(
    val kty: String,       // Key Type（例: "RSA"）
    val use: String?,      // 使用目的（"sig"=署名検証）
    val kid: String?,      // Key ID
    val alg: String?,      // アルゴリズム（例: "RS256"）
    val n: String?,        // RSA: modulus
    val e: String?,        // RSA: exponent
    val x: String?,        // EC: x座標
    val y: String?,        // EC: y座標
    val crv: String?,      // EC: curve名
)
```

### RegisterTokenRecord（DBに保存される登録トークン情報）

```kotlin
data class RegisterTokenRecord(
    val token: RegisterToken,
    val creatorIdentityId: IdentityId,  // トークン発行者（idServerA 側ユーザー）
    val expiresAtEpochMs: Long,          // 有効期限（epoch ms）
)
```

---

## 3. ポートインターフェース（ドメイン層）

kise サーバーがこれらを実装する。

| インターフェース | シグネチャ | 役割 |
|---|---|---|
| `JwksRepository` | `get(url: JwksUrl): Res<Jwks, KicpErr>` | URLからJWKS取得（キャッシュはinfra実装） |
| `JwtVerifier` | `verify(rawToken: String, jwks: Jwks): Res<TokenClaims, KicpErr>` | JWTをJWKSで検証 → TokenClaims |
| `RegisterTokenRepository` | `save/find/delete(...)` | 登録トークンの永続化 |
| `RegisterTokenGenerator` | `generate(): RegisterToken` | ランダムなRegisterToken生成 |
| `CurrentTimeMs` | `now(): Long` | 現在時刻（epoch ms） |
| `PeerServerClient` | `verifyRegister(registerId, registerToken): Res<Unit, KicpErr>` | ピアサーバーへの検証呼び出し |

---

## 4. ユースケース

### 4.1 LoginUseCase（idServerA / idServerB 共通）

oidcToken と providerToken の両方を検証して `IdentityId` を返す。

```kotlin
interface LoginUseCase {
    suspend fun login(input: LoginInput): Res<IdentityId, KicpErr>
}

data class LoginInput(
    val oidcToken: OidcToken,
    val oidcJwksUrl: JwksUrl,
    val providerToken: ProviderToken,
    val providerJwksUrl: JwksUrl,
)
```

**処理ステップ:**

```
1. JwksRepository.get(providerJwksUrl)      → providerJwks
2. JwtVerifier.verify(providerToken, providerJwks)  → 検証のみ（Claimsは不使用）
3. JwksRepository.get(oidcJwksUrl)          → oidcJwks
4. JwtVerifier.verify(oidcToken, oidcJwks)  → oidcClaims（issuer, subject）
5. IdentityId("{oidcClaims.issuer}:{oidcClaims.subject}") を返却
```

### 4.2 GetRegisterTokenUseCase（idServerA 側）

認証済みの `IdentityId` に対して有効期限付き `RegisterToken` を発行する。

```kotlin
interface GetRegisterTokenUseCase {
    suspend fun getRegisterToken(input: GetRegisterTokenInput): Res<RegisterToken, KicpErr>
}

data class GetRegisterTokenInput(
    val identityId: IdentityId,
    val validForMs: Long = 5 * 60 * 1000L,  // デフォルト5分
)
```

**処理ステップ:**

```
1. RegisterTokenGenerator.generate()        → token（ランダム生成）
2. RegisterTokenRecord {                    → レコード作成
     token,
     creatorIdentityId = input.identityId,
     expiresAtEpochMs = now() + validForMs
   }
3. RegisterTokenRepository.save(record)     → 保存
4. token を返却 → クライアントへ渡す
```

### 4.3 RegisterUseCase（idServerB 側）

idServerB でユーザー登録を処理する。idServerA への検証呼び出しを含む。

```kotlin
interface RegisterUseCase {
    suspend fun register(input: RegisterInput): Res<IdentityId, KicpErr>
}

data class RegisterInput(
    val oidcToken: OidcToken,
    val oidcJwksUrl: JwksUrl,
    val providerToken: ProviderToken,
    val providerJwksUrl: JwksUrl,
    val registerToken: RegisterToken,
)
```

**処理ステップ:**

```
1. JwksRepository.get(providerJwksUrl)      → providerJwks
2. JwtVerifier.verify(providerToken, ...)   → 検証のみ
3. JwksRepository.get(oidcJwksUrl)          → oidcJwks
4. JwtVerifier.verify(oidcToken, oidcJwks)  → oidcClaims
5. RegisterId("{oidcClaims.issuer}:{oidcClaims.subject}") を生成
6. PeerServerClient.verifyRegister(registerId, registerToken)
   └─ idServerA の VerifyRegisterTokenUseCase を呼び出す
7. IdentityId("{oidcClaims.issuer}:{oidcClaims.subject}") を返却
```

### 4.4 VerifyRegisterTokenUseCase（idServerA 側、ピアからの呼び出しを処理）

idServerB からの検証リクエストを受けて、RegisterToken を検証・削除し、元の IdentityId を返す。

```kotlin
interface VerifyRegisterTokenUseCase {
    suspend fun verify(input: VerifyRegisterInput): Res<IdentityId, KicpErr>
}

data class VerifyRegisterInput(
    val registerId: RegisterId,
    val registerToken: RegisterToken,
    val currentTimeMs: Long,
)
```

**処理ステップ:**

```
1. RegisterTokenRepository.find(registerToken)
   └─ null → RegisterTokenNotFoundErr（使用済みまたは未発行）
2. record.expiresAtEpochMs < currentTimeMs の場合:
   → RegisterTokenRepository.delete(token)（期限切れも削除）
   → RegisterTokenExpiredErr
3. RegisterTokenRepository.delete(token)   ← リプレイ防止のため即削除
4. record.creatorIdentityId を返却         → idServerB へ
```

---

## 5. モジュール構成

```
kicp/
├── kicp-domain/                           # KMP（JVM, JS）
│   └── src/commonMain/kotlin/.../kicp/domain/
│       ├── KicpDomain.kt                  # ドメインオブジェクト
│       ├── claims/
│       │   └── TokenClaims.kt
│       ├── err/
│       │   └── KicpErr.kt                 # エラー型階層
│       ├── identity/
│       │   ├── IdentityId.kt
│       │   └── RegisterId.kt
│       ├── jwks/
│       │   ├── Jwks.kt
│       │   ├── JwkKey.kt
│       │   └── JwksUrl.kt
│       ├── repo/                           # ポートインターフェース
│       │   ├── CurrentTimeMs.kt
│       │   ├── JwksRepository.kt
│       │   ├── JwtVerifier.kt
│       │   ├── PeerServerClient.kt
│       │   ├── RegisterTokenGenerator.kt
│       │   ├── RegisterTokenRecord.kt
│       │   └── RegisterTokenRepository.kt
│       └── token/
│           ├── OidcToken.kt
│           ├── ProviderToken.kt
│           └── RegisterToken.kt
│
└── kicp-usecase/                          # KMP（JVM, JS）
    └── src/commonMain/kotlin/.../kicp/usecase/
        ├── login/
        │   ├── LoginInput.kt
        │   └── LoginUseCase.kt            # + LoginUseCaseImpl
        └── register/
            ├── GetRegisterTokenInput.kt
            ├── GetRegisterTokenUseCase.kt  # + GetRegisterTokenUseCaseImpl
            ├── RegisterInput.kt
            ├── RegisterUseCase.kt          # + RegisterUseCaseImpl
            ├── VerifyRegisterInput.kt
            └── VerifyRegisterTokenUseCase.kt # + VerifyRegisterTokenUseCaseImpl
```

---

## 6. エラー型

```kotlin
sealed class KicpErr(message: String, cause: Throwable? = null) : Exception(message, cause)

class JwksFetchErr(url: String, cause: Throwable?)
    : KicpErr("Failed to fetch JWKS from $url", cause)
// JWKS取得失敗（ネットワークエラー等）

class TokenVerificationErr(message: String, cause: Throwable?)
    : KicpErr(message, cause)
// JWT署名検証失敗（改ざん、期限切れ等）

class RegisterTokenNotFoundErr
    : KicpErr("Register token not found or already used")
// 登録トークンが見つからない（使用済み or 未発行）

class RegisterTokenExpiredErr
    : KicpErr("Register token has expired")
// 登録トークンが有効期限切れ

class PeerVerificationErr(message: String, cause: Throwable?)
    : KicpErr(message, cause)
// idServerA への検証呼び出しが失敗
```

---

## 7. 設計上の特性

### 二重トークン検証

ログイン・登録の両方で **OIDCトークン + プロバイダートークンの2つを必須検証**：

- **OIDCトークン**: ユーザーの本人確認（標準的なJWT検証）
- **プロバイダートークン**: プロバイダーの認証（プロバイダー固有のJWT）

両方の検証が通らないと `IdentityId` を返さない。

### 登録トークンの安全設計

| 特性 | 詳細 |
|---|---|
| 有効期限 | デフォルト5分（`GetRegisterTokenInput.validForMs`） |
| 使用回数 | 1回限り（`VerifyRegisterTokenUseCase` で検証後即削除） |
| 期限切れ処理 | 期限切れトークンも検証時に削除（蓄積防止） |
| リプレイ防止 | 削除後は `RegisterTokenNotFoundErr` で弾く |

### `IdentityId` の一意性

```
IdentityId("{issuer}:{subject}")
```

- `issuer`: OIDCプロバイダーのURL（例: `https://accounts.google.com`）
- `subject`: プロバイダー内のユーザー識別子（例: `abc123`）

異なるIdPの同一ユーザーは別の `IdentityId` を持つ。クロスドメイン登録でこれらを紐づける。

### Clean Architecture の適用

```
kicp-domain （外部依存なし）
    ↑ 依存
kicp-usecase（domain のインターフェースのみ使用）
    ↑ 依存
kise 等の infra 層（具体的なHTTP・DB実装）
```

ドメイン層とユースケース層は KMP 対応（JVM・JS どちらでも動作）。
