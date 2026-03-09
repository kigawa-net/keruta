ktcl-webモジュールにKeycloakログイン機能を追加しました。

変更点：
- buildSrc/src/main/kotlin/ktor-server.gradle.ktsにktor-server-html-builder依存関係を追加（HTML生成用）
- ktcl-web/src/main/kotlin/net/kigawa/keruta/ktcl/web/KerutaTaskClientWeb.ktを編集：
  - ログインページ（"/login"）に「Login with Keycloak」リンクを追加
  - 新しいGET "/auth/keycloakClient"エンドポイントを追加し、KeycloakのOIDC authorize URLにリダイレクト
  - 新しいGET "/auth/callback"エンドポイントを追加し、Keycloakからの認証コードを受け取り成功ページを表示
- 設定は環境変数から取得（ktor.security.keycloakClient.*）

これにより、ユーザーはログインページからKeycloak認証を選択できるようになりました。完全なOAuth2フローの実装は今後の拡張で可能。
