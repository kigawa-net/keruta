ktcl-webモジュールに設定管理用のConfigクラスを作成しました。

作成したファイル：
- ktcl-web/src/main/kotlin/net/kigawa/keruta/ktcl/web/Config.kt：設定を読み取るデータクラス

変更点：
- Configクラス：JwtConfigとKeycloakConfigを統合して一つのクラスにまとめ、直接フィールドを持つように変更
- Config：application.yamlのktor.security.jwtとktor.security.keycloakから設定を読み取り
- KerutaTaskClientWeb.kt：Configを読み込んで各モジュールに渡す
- JwtModule.kt：Configを受け取ってJWT認証を設定
- application.yaml：keycloak設定を追加（url, realm, clientId, clientSecret）

これにより、設定の一元管理が可能になり、コードがシンプルになりました。