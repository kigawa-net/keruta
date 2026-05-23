# KICP: クロスドメインIDフェデレーション

## 概要

KICP (Kigawa Identity Cross-domain Protocol) は、異なるドメインの idServer 間でユーザーIDをフェデレーションするプロトコル。OIDCトークンとプロバイダートークンの両方を検証し、登録トークンを使ってクロスドメイン登録を行う。

## フロー

```
clientA ──login──► oidcA          → oidc access token
clientA ──get──────► providerA    → provider token
clientA ──login──► idServerA      (oidcToken + providerToken)
  idServerA: providerToken 検証 (JWKS from providerA)
  idServerA: oidcToken 検証 (JWKS from oidcA)
idServerA ──► clientA             → authed

clientA ──get register token──► idServerA  → registerToken
clientA ──redirect──► clientB     (registerToken)

clientB ──login──► oidcB          → oidc access token
clientB ──get──────► providerB    → provider token
clientB ──register──► idServerB   (oidcToken + providerToken + registerToken)
  idServerB: providerToken 検証
  idServerB: oidcToken 検証
  idServerB ──verify──► idServerA  (registerId + registerToken)
    idServerA: registerToken 検証・削除（リプレイ防止）
  idServerA ──► idServerB          → success
idServerB ──► clientB             → authed
```

## モジュール構成

```
kicp/
├── kicp-domain/    net.kigawa.keruta.kicp.domain
│   ├── token/      OidcToken, ProviderToken, RegisterToken
│   ├── identity/   IdentityId, RegisterId
│   ├── jwks/       Jwks, JwkKey, JwksUrl
│   ├── claims/     TokenClaims (issuer, subject, audience)
│   ├── err/        KicpErr 階層
│   └── repo/       ポートインターフェース一覧（下表）
└── kicp-usecase/   net.kigawa.keruta.kicp.usecase
    ├── login/      LoginUseCase
    └── register/   GetRegisterTokenUseCase, RegisterUseCase,
                    VerifyRegisterTokenUseCase
```

## ドメインポート（repo/）

| インターフェース | 役割 |
|---|---|
| `JwksRepository` | URL から JWKS 取得（キャッシュはインフラ実装） |
| `JwtVerifier` | raw JWT + JWKS → `TokenClaims` |
| `RegisterTokenRepository` | 登録トークンの保存 / 検索 / 削除 |
| `RegisterTokenGenerator` | ランダムな `RegisterToken` 生成 |
| `CurrentTimeMs` | 現在時刻（epoch ms）取得 |
| `PeerServerClient` | 相手 idServer への登録トークン検証呼び出し |

## ユースケース

| ユースケース | 動作サーバー | 処理 |
|---|---|---|
| `LoginUseCase` | 両方 | providerToken + oidcToken 検証 → `IdentityId` |
| `GetRegisterTokenUseCase` | idServerA | 認証済みIDに有効期限付き登録トークン発行 |
| `RegisterUseCase` | idServerB | トークン検証 → `PeerServerClient.verifyRegister()` 呼び出し |
| `VerifyRegisterTokenUseCase` | idServerA | 登録トークン検証・削除 → 元 `IdentityId` 返却 |

## 設計上の特性

- `IdentityId` は OIDC の `issuer:subject` から生成
- `RegisterId` は登録クライアントの `issuer:subject`（= `IdentityId` の値）
- 登録トークンは1回使用で即削除（リプレイ攻撃防止）
- デフォルト有効期限: 5分（`GetRegisterTokenInput.validForMs`）

## エラー型

```
KicpErr
  ├── JwksFetchErr          — JWKS取得失敗
  ├── TokenVerificationErr  — JWT検証失敗
  ├── RegisterTokenNotFoundErr — トークン未発見 or 使用済み
  ├── RegisterTokenExpiredErr  — トークン期限切れ
  └── PeerVerificationErr   — ピアサーバー検証失敗
```
