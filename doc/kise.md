# Keruta ID Server 設計書

> **ステータス**: 計画段階（未実装）
>
> **kise** は Keruta プロジェクトの **infra層（認証基盤）** モジュールです。既存のktse認証機能を拡張・分離するために計画されており、複数のサービス（ktse, ktcl-k8s, kicl-webなど）で统一された認証メカニズムを提供します。現在のktse認証は `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/` に実装されています。

## 概要

Keruta ID Serverは、Kerutaプロジェクトの統合認証基盤として機能する独立モジュールです。既存のKtse認証機能を拡張し、複数のサービス間で统一されたユーザー認証とアイデンティティ管理を提供します。

### 目標

1. **認証の統合** — 複数のサービス（ktse, ktcl-k8s, kicl-webなど）で统一された認証メカニズム
2. **ユーザビリティの向上** — OIDC（OpenID Connect）に準拠した標準的な認証フロー
3. **スケーラビリティ** — 分散システムに対応できる認証基盤
4. **保守性** — 認証ロジックの単一化管理

### 関連ドキュメント

- [authentication.md](authentication.md) — 現在のktse認証実装
- [kicp.md](kicp.md) — クロスドメインIDフェデレーション（kiseと密的相關）

## システム構成

### 全体アーキテクチャ

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Applications                     │
│  (ktcl-k8s, kicl-web, ktcl-front, etc.)                    │
└─────────────────────────┬───────────────────────────────────┘
                          │ KTCP WebSocket + JWT
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     Keruta ID Server (kise)                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ JWT Issuer  │  │ OIDC Router │  │ Session Manager    │  │
│  │             │  │             │  │                   │  │
│  │ - Token Gen │  │ - Provider │  │ - Session Store   │  │
│  │ - Validate  │  │ - Discovery│  │ - Timeout        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────┬───────────────────────────────────┘
                          │
              ┌───────────┴───────────┐
              ▼                       ▼
┌─────────────────────┐    ┌─────────────────────────────┐
│   External IdPs     │    │      Database (MySQL)       │
│  (Auth0, Keycloak,  │    │  - user, user_idp, provider │
│   Google, etc.)     │    │  - session, token_log      │
└─────────────────────┘    └─────────────────────────────┘
```

### モジュール構成（infra層）

kise は infra 層モジュールであり、配下に2つのサブモジュールを持ちます:

| モジュール | 役割 |
|-----------|------|
| `kise` | Ktor WebSocketサーバー（エントリーポイント） |
| `kise:domain` | ドメインエンティティとインターフェース |
| `kise:usecase` | ユースケース実装 |

### 依存関係

```
kise               # エントリーポイント（JVMメイン）
  ├── kise:domain
  ├── kise:usecase
  ├── kodel:coroutine
  └── ktor-server-websocket

kise:domain
  └── kodel:api (Res, EntrypointDeferred)

kise:usecase
  ├── kise:domain
  └── kodel:api
```

## 機能仕様

### 1. 認証機能

#### 1.1 ユーザートークン検証（OIDC）

- **入力**: ユーザーのIdPが発行したJWT
- **処理**:
  1. JWKをキャッシュから取得（LRUキャッシュ、最大8発行者）
  2. 署名検証（RSA256）
  3. 有効期限、issuer、audience検証
  4. サブジェクト抽出
- **出力**: 検証結果（成功/失敗）とユーザー情報

#### 1.2 プロバイダートークン検証

- **入力**: プロバイダーのIdPが発行したJWT
- **処理**: DB照合による提供者検証
- **出力**: 検証結果と提供者情報

#### 1.3 トークン発行

- **アルゴリズム**: RS256（RSA署名）
- **有効期限**: 3時間（設定可能）
- **クレーム**:
  ```json
  {
    "sub": "user_id",
    "iss": "https://id.kigawa.net",
    "aud": "ktse",
    "exp": 1234567890,
    "iat": 1234567890,
    "provider": "auth0"
  }
  ```

### 2. セッション管理

#### 2.1 セッション確立

- **タイムアウト**: 1時間（無操作で自動切断）
- **最大同時セッション**: 100ユーザー/アカウント
- **ステータス**: active, expired, revoked

#### 2.2 セッションストア

- データベース（MySQL）にセッションデータを保存
- メモリキャッシュ（LRU）による高速アクセス

### 3. OIDC Router

複数のIdPを一元管理:

| IdP | 設定項目 |
|-----|----------|
| Auth0 | issuer, audience, client_id, client_secret |
| Keycloak | issuer, realm, client_id, client_secret |
| Google | issuer, audience, client_id |

### 4. KICP（クロスドメインIDフェデレーション）

kiseは[KICPプロトコル](kicp.md)に基づくクロスドメインIDフェデレーションをサポートします。

#### 4.1 登録トークンフロー

1. ユーザーがidServerAで認証
2. `GetRegisterTokenUseCase` で登録トークン発行（有効期限5分）
3. ユーザーは登録トークンを携带してidServerBへリダイレクト
4. idServerBで `RegisterUseCase` が登録トークンを検証
5. idServerB → idServerAへ `VerifyRegisterTokenUseCase` を呼び出し
6. 検証成功後、idServerBでユーザーが作成/関連付け

#### 4.2 アイデンティティマッピング

```
IdentityId = issuer:subject  （OIDCの issuer + sub）
RegisterId = 登録元の IdentityId
```

### 5. ユーザー管理

#### 5.1 ユーザー登録

- 初回認証時に自動登録
- 既存ユーザーは再利用（subject + issuerで一意 Identification）

#### 5.2 ユーザー情報更新

- IdPからの情報で自动更新
- 変更可能なフィールド: name, email, picture

## データモデル

### ER図

```
user (1) ─────< (N) user_idp
  │
  ├─────< (N) session
  │
  ├─────< (N) provider
  │              │
  ├─────< (N) queue_user >──── (N) queue
  │
  └─────< (N) task
```

### テーブル定義

#### user — システムユーザー

| カラム | 型 | NULL | 説明 |
|-------|-----|------|------|
| id | BIGINT PK | NO | 内部ID |
| sub | VARCHAR(255) | NO | IdPサブジェクト |
| issuer | VARCHAR(255) | NO | IdP発行者 |
| name | VARCHAR(255) | YES | 表示名 |
| email | VARCHAR(255) | YES | メールアドレス |
| picture | VARCHAR(512) | YES | アバターURL |
| create_at | TIMESTAMP | NO | 作成日時 |
| update_at | TIMESTAMP | NO | 更新日時 |

UNIQUE(sub, issuer)

#### session — アクティブセッション

| カラム | 型 | NULL | 説明 |
|-------|-----|------|------|
| id | BIGINT PK | NO | |
| user_id | BIGINT FK→user | NO | |
| token | VARCHAR(512) | NO | JWTトークン |
| expires_at | TIMESTAMP | NO | 有効期限 |
| create_at | TIMESTAMP | NO | |
| update_at | TIMESTAMP | NO | |

#### provider — 認証プロバイダー設定

| カラム | 型 | NULL | 説明 |
|-------|-----|------|------|
| id | BIGINT PK | NO | |
| user_id | BIGINT FK→user | NO | |
| issuer | VARCHAR(255) | NO | |
| audience | VARCHAR(255) | NO | |
| name | VARCHAR(255) | NO | |
| config | JSON | YES | プロバイダー固有設定 |
| create_at | TIMESTAMP | NO | |

#### token_log — トークン利用履歴

| カラム | 型 | NULL | 説明 |
|-------|-----|------|------|
| id | BIGINT PK | NO | |
| user_id | BIGINT FK→user | NO | |
| action | VARCHAR(50) | NO | create, verify, refresh |
| ip_address | VARCHAR(45) | YES | IPアドレス |
| user_agent | VARCHAR(255) | YES | |
| create_at | TIMESTAMP | NO | |

## メッセージフロー

### 認証リクエストフロー

```
Client                      kise Server
   │                             │
   │── Connect ─────────────────▶│
   │                             │
   │── AuthRequestMsg ──────────▶│
   │   (user_token,              │
   │    provider_token)          │
   │                             │── Verify user token
   │                             │── Verify provider token
   │                             │── Create/Update session
   │                             │
   │<── AuthResponseMsg ─────────│
   │   (success/error, session)│
   │                             │
   │── (Authenticated) ────────▶│
```

### メッセージ型

```kotlin
@Serializable
sealed interface ClientMsg {
    @Serializable
    @SerialName("auth_request")
    data class AuthRequestMsg(
        val userToken: String,
        val providerToken: String,
    ): ClientMsg
    
    @Serializable
    @SerialName("session_refresh")
    data class SessionRefreshMsg(
        val sessionToken: String,
    ): ClientMsg
}

@Serializable
sealed interface ServerMsg {
    @Serializable
    @SerialName("auth_response")
    data class AuthResponseMsg(
        val sessionToken: String,
        val expiresAt: Long,
    ): ServerMsg
    
    @Serializable
    @SerialName("auth_error")
    data class AuthErrorMsg(
        val code: String,
        val message: String,
    ): ServerMsg
}
```

## API設計

### エントリーポイント

```kotlin
interface AuthEntrypoint<C, A> {
    suspend fun exec(arg: A, ctx: C): EntrypointDeferred<Res<Unit, KiseErr>>
}

// 認証リクエスト
interface AuthRequestEntrypoint<C>: AuthEntrypoint<C, AuthRequestArg> {
    override suspend fun exec(arg: AuthRequestArg, ctx: C): EntrypointDeferred<Res<AuthResponse, KiseErr>>
}

// セッション更新
interface SessionRefreshEntrypoint<C>: AuthEntrypoint<C, SessionRefreshArg> {
    override suspend fun exec(arg: SessionRefreshArg, ctx: C): EntrypointDeferred<Res<SessionRefreshResponse, KiseErr>>
}

// ログアウト
interface LogoutEntrypoint<C>: AuthEntrypoint<C, LogoutArg> {
    override suspend fun exec(arg: LogoutArg, ctx: C): EntrypointDeferred<Res<Unit, KiseErr>>
}

// ユーザー情報取得
interface GetUserEntrypoint<C>: AuthEntrypoint<C, GetUserArg> {
    override suspend fun exec(arg: GetUserArg, ctx: C): EntrypointDeferred<Res<User, KiseErr>>
}
```

### エラー型

```kotlin
sealed interface KiseErr : Throwable {
    @Serializable
    data class InvalidTokenErr(val message: String): KiseErr
    
    @Serializable
    data class TokenExpiredErr(val message: String): KiseErr
    
    @Serializable
    data class ProviderNotFoundErr(val message: String): KiseErr
    
    @Serializable
    data class SessionNotFoundErr(val message: String): KiseErr
    
    @Serializable
    data class UserNotFoundErr(val message: String): KiseErr
}
```

## セキュリティ設計

### 1. トークンセキュリティ

- **署名アルゴリズム**: RS256（RSA 2048-bit）
- **有効期限**: 3時間（短め）
- **発行者**: `https://id.kigawa.net`
- ** audience**: サービス固有（ktse, ktcl-k8s, kicl-web）

### 2. セッションセキュリティ

- **タイムアウト**: 1時間（無操作で切断）
- **最大同時セッション**: 10/ユーザー
- **IPバインディング**: オプション

### 3. 監査ログ

- すべての認証試行を記録
- 失敗回数の tracking（30分钟内3回まで）
- 超過場合は自動ブロック

## 設定（application.yaml）

```yaml
kise:
  issuer: "https://id.kigawa.net"
  token:
    expires-in: 10800000  # 3時間（ミリ秒）
    algorithm: RS256
    key-size: 2048
  session:
    timeout: 3600000      # 1時間（ミリ秒）
    max-per-user: 10
  providers:
    - name: "auth0"
      issuer: "https://example.auth0.com/"
      audience: "https://api.example.com"
      jwks-url: "https://example.auth0.com/.well-known/jwks.json"
    - name: "keycloak"
      issuer: "https://keycloak.kigawa.net/realms/keruta"
      audience: "keruta-api"
```

## 未実装機能（future）

1. **リフレッシュトークン** — 自動更新メカニズム
2. **多要素認証（MFA）** — 追加のセキュリティ層
3. **OAuth 2.0認可** — 第三方アプリケーション授权
4. **ソーシャルログイン** — Google, GitHub等
5. **パスワードリセット** — パスワード変更フロー

## 実装ロードマップ（infra層として）

kise は独立した infra 層モジュールとして実装され、他のサービス（ktse, ktcl-k8s, kicl-web）から認証サービスとして呼び出されます。

### Phase 1: 基本認証機能（優先度: 高）

1. **kise:domain** モジュールの作成
   - エンティティ定義（User, Session, Provider, TokenLog）
   - ユースケースインターフェース定義
   - エラー型（KiseErr）の定義

   ```
   kise-domain/
   └── src/commonMain/kotlin/net/kigawa/keruta/kise/domain/
       ├── entity/                 # User, Session, Provider, TokenLog
       ├── repository/             # ポートインターフェース
       └── error/                  # KiseErr 階層
   ```

2. **kise:usecase** モジュールの作成
   - ユーザートークン検証ユースケース
   - プロバイダートークン検証ユースケース
   - セッション管理ユースケース
   - `Res<T, E>` パターンを適用した実装

   ```
   kise-usecase/
   └── src/commonMain/kotlin/net/kigawa/keruta/kise/usecase/
       ├── auth/                   # VerifyUserTokenUseCase, VerifyProviderTokenUseCase
       └── session/                # CreateSessionUseCase, RefreshSessionUseCase
   ```

3. **既存コードとの統合**
   - `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/` からロジックを移植
   - `Res<T, E>` パターンを適用したリファクタリング

### Phase 2: サーバー機能（優先度: 中）

4. **kise** モジュールの作成（エントリーポイント）
   - Ktor WebSocketサーバー実装
   - JWT発行機能（RS256）
   - セッション管理機能
   - KTCPプロトコル対応

   ```
   kise/                           # エントリーポイント（infra層）
   ├── build.gradle.kts           # ktor-server-websocket 依存
   └── src/jvmMain/kotlin/net/kigawa/keruta/kise/
       ├── websocket/              # KtcpServer 実装
       ├── jwt/                    # JwtIssuer, JwtVerifier
       └── entrypoint/             # AuthEntrypoint実装
   ```

5. **ktse側の更新**
   - 外部サービスとしてkiseを使用するように更新
   - KTCPプロトコルでの連携

### Phase 3: KICP統合（優先度: 中）

7. **クロスドメイン対応**
   - [kicp.md](kicp.md) で定義されたKICPプロトコルの実装
   - 異なるドメイン間のIDフェデレーション
   - 登録トークンによるユーザー マッピング

### Phase 4: 拡張機能（優先度: 低）

- リフレッシュトークン
- 多要素認証（MFA）
- OAuth 2.0認可
- ソーシャルログイン
- パスワードリセット

## 移行 plan

既存のKtse認証からKiseへの移行:

1. 現在の `KtseJwtVerifier` を `KiseJwtVerifier` に置き換え
2. データベーススキーマを更新（マイグレーション）
3. 認証フローをKiseにリダイレクト
4. 段階的にサービスを切り替え

> **注意**: 移行時は既存の認証データを完全に維持する必要があります。ユーザーテーブルの `sub` + `issuer` で一意に識別される方式是重要です。

## Appendix

### 用語集

| 用語 | 説明 |
|-----|------|
| IdP | Identity Provider（認証提供者） |
| OIDC | OpenID Connect（標準認証プロトコル） |
| JWT | JSON Web Token（トークン形式） |
| JWK | JSON Web Key（公開鍵形式） |
| Session | 認証済みユーザーのsession |

### 関連ファイル

- `kise/` — モジュールソース（計画中）
- `doc/authentication.md` — 現在のktse認証 design
- `doc/database.md` — データベース design
- `doc/kicp.md` — KICP（クロスドメインIDフェデレーション）プロトコル
- `ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/` — 既存の認証実装（リファレンス）

### 現在の実装からの参照

既存の認証機能は以下にあります:

```
ktse/src/main/kotlin/net/kigawa/keruta/ktse/auth/
├── JwtVerifier.kt           # JWT検証基底クラス
├── Auth0JwtVerifier.kt      # Auth0用検証実装
├── ProviderVerifier.kt      # プロバイダートークン検証
├── UserVerifier.kt          # ユーザ作成・再利用
├── AuthManager.kt           # 認証ライフサイクル管理
└── SessionManager.kt        # セッション管理
```

これらのクラスをベースとして、kiseモジュールの実装が進められます。