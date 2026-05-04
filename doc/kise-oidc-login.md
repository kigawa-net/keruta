# Kise OIDCログイン機能

## 概要

kiseモジュールにOIDC（OpenID Connect）ログイン機能を追加しました。これにより、ユーザーは既存のOIDCプロバイダー（Keycloak、Auth0など）を使用して認証できるようになります。

## アーキテクチャ

### 認証フロー

1. ユーザーが `/login` にアクセス
2. kiseがOIDCプロバイダーの認可エンドポイントにリダイレクト（PKCE付き）
3. ユーザーがOIDCプロバイダーで認証
4. OIDCプロバイダーが `/callback` に認可コードを返す
5. kiseが認可コードをアクセストークンとIDトークンに交換
6. IDトークンを検証し、ユーザーセッションを作成

### 主要コンポーネント

#### モデルクラス
- `Pkce`: PKCE（Proof Key for Code Exchange）情報を保持
- `OidcSession`: セッション中のOIDC情報を保持
- `TokenResponse`: トークンエンドポイントからのレスポンス
- `OidcDiscoveryResponse`: OIDCディスカバリーレスポンス

#### サービスクラス
- `OidcDiscoveryFetcher`: OIDCディスカバリーエンドポイントから設定を取得
- `PkceGenerator`: PKCEパラメータを生成
- `IdTokenVerifier`: IDトークンを検証

#### ルート
- `LoginRoute`: `/login` エンドポイント（OIDC認可フロー開始）
- `CallbackRoute`: `/callback` エンドポイント（OIDCコールバック処理）

## 設定

`kise/src/jvmMain/resources/application.conf` を編集して設定を行います：

```hocon
kise {
    # ... 既存の設定 ...

    oidc {
        clientId = "kise-client"           # OIDCクライアントID
        redirectUri = "http://localhost:8080/callback"  # コールバックURI
    }
}
```

また、OIDCプロバイダーの設定も必要です：

```hocon
ktor {
    security {
        defaultIdp {
            issuer = "https://id.kigawa.net"  # OIDCプロバイダーのissuer
            audience = "keruta"
        }
    }
}
```

## 使用方法

### ログイン開始

ユーザーを `/login` にリダイレクトします：

```
GET /login
```

または、クエリパラメータでカスタマイズ可能：

```
GET /login?issuer=https://keycloak.example.com/realms/myrealm&clientId=my-client&redirect_uri=http://localhost:8080/callback
```

### コールバック

OIDCプロバイダーが認可コードを返すエンドポイント：

```
GET /callback?code=...&state=...
```

## 必要な依存関係

kiseの `build.gradle.kts` に以下の依存関係が追加されました：

```kotlin
// Ktor Server
implementation("io.ktor:ktor-server-netty-jvm:${Version.KTOR}")
implementation("io.ktor:ktor-server-sessions-jvm:${Version.KTOR}")
implementation("io.ktor:ktor-server-auth-jvm:${Version.KTOR}")
implementation("io.ktor:ktor-server-content-negotiation-jvm:${Version.KTOR}")

// Ktor Client
implementation("io.ktor:ktor-client-core-jvm:${Version.KTOR}")
implementation("io.ktor:ktor-client-cio-jvm:${Version.KTOR}")
implementation("io.ktor:ktor-client-content-negotiation-jvm:${Version.KTOR}")

// JWT検証
implementation("com.auth0:jwks-rsa:${Version.JWKS_RSA}")
```

## テスト

単体テストが実装されています：

```bash
./gradlew :kise:jvmTest
```

### テストクラス

- `PkceGeneratorTest`: PKCE生成のテスト
- `OidcDiscoveryFetcherTest`: OIDCディスカバリーレスポンス解析のテスト
- `LoginRouteTest`: ログインルートの統合テスト（簡易版）

## 注意点

1. **セッション管理**: OIDCセッション情報はKtorのセッション機能で管理されます
2. **PKCE**: セキュリティ強化のため、PKCE（S256）を使用しています
3. **トークン検証**: IDトークンはJWKSを使用して検証されます
4. **設定**: 本番環境では適切なOIDCプロバイダーの設定が必要です

## 今後の拡張

- [ ] リフレッシュトークンのサポート
- [ ] ユーザー情報（userinfo）の取得・保存
- [ ] 複数のOIDCプロバイダーのサポート
- [ ] ログアウト機能の実装
