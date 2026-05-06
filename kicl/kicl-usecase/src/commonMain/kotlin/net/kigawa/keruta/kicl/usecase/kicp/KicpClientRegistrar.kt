package net.kigawa.keruta.kicl.usecase.kicp

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * kicpクライアント登録のためのブラウザ用エントリーポイント
 */
@JsExport
class KicpClientRegistrar {
    /**
     * kicpクライアントを登録する（モック実装）
     * 
     * @param registerUrl 登録APIのURL
     * @param oidcToken OIDCトークン
     * @param providerToken プロバイダートークン
     * @param registerToken 登録トークン
     * @param callback 結果を受け取るコールバック (success: Boolean, message: String)
     */
    @JsName("register")
    fun register(
        registerUrl: String,
        oidcToken: String,
        providerToken: String,
        registerToken: String,
        callback: (Boolean, String) -> Unit
    ) {
        // モック実装: 実際のHTTP通信は今後の課題
        console.log("KicpClientRegistrar.register called with url: $registerUrl")
        // 成功をシミュレート
        callback(true, "登録リクエストを受け付けました（モック）")
    }

    /**
     * 登録トークンを取得する（モック実装）
     */
    @JsName("getRegisterToken")
    fun getRegisterToken(issuerUrl: String, callback: (Boolean, String) -> Unit) {
        console.log("KicpClientRegistrar.getRegisterToken called with issuerUrl: $issuerUrl")
        callback(true, "登録トークン要求を受け付けました（モック）")
    }
}
