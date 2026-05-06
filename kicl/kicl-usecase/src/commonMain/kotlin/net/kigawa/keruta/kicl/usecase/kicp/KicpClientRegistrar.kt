package net.kigawa.keruta.kicl.usecase.kicp

import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * kicpクライアント登録のためのブラウザ用エントリーポイント
 */
@JsExport
class KicpClientRegistrar {
    /**
     * kicpクライアントを登録する
     * サーバーサイドの登録エンドポイントを呼び出す
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
        // ブラウザ環境でのfetchを使用
        val body = """
            {
                "oidcToken": "$oidcToken",
                "providerToken": "$providerToken",
                "registerToken": "$registerToken"
            }
        """.trimIndent()

        // Kotlin/JSのfetch APIを使用
        try {
            val response = js("fetch")(registerUrl, object {
                val method = "POST"
                val headers = js("({ 'Content-Type': 'application/json' })")
                val body = body
            })

            val jsonPromise = js("Promise.resolve")(response).then { res ->
                js("res.json()")
            }

            js("Promise.resolve")(jsonPromise).then { result ->
                callback(true, "登録リクエストを送信しました: $result")
            }?.`catch` { err ->
                callback(false, "エラー: $err")
            }
        } catch (e: Exception) {
            callback(false, "例外が発生しました: ${e.message}")
        }
    }

    /**
     * 登録トークンを取得する（ダミー実装）
     */
    @JsName("getRegisterToken")
    fun getRegisterToken(issuerUrl: String, callback: (Boolean, String) -> Unit) {
        // 実際にはサーバーにトークン発行を依頼する
        callback(true, "トークン取得のリクエストを送信しました")
    }
}
