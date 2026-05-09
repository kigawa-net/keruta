package net.kigawa.keruta.kicl.usecase.kicp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@Serializable
private data class RegisterBody(
    val oidcToken: String,
    val providerToken: String,
    val registerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)

@Serializable
private data class GetRegisterTokenBody(
    val oidcToken: String,
    val providerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)

@Serializable
private data class KicpApiResponse(
    val success: Boolean,
    val message: String? = null,
    val identityId: String? = null,
    val registerToken: String? = null,
)

/**
 * kicpクライアント登録のためのブラウザ用エントリーポイント
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class KicpClientRegistrar {
    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
    }

    /**
     * kicpクライアントを登録する
     *
     * @param registerUrl 登録APIのURL（/api/kicp/register）
     * @param oidcToken OIDCトークン
     * @param providerToken プロバイダートークン
     * @param registerToken 登録トークン
     * @param oidcJwksUrl OIDC JWKSのURL
     * @param providerJwksUrl プロバイダー JWKSのURL
     * @param callback 結果を受け取るコールバック (success: Boolean, message: String)
     */
    @JsName("register")
    fun register(
        registerUrl: String,
        oidcToken: String,
        providerToken: String,
        registerToken: String,
        oidcJwksUrl: String,
        providerJwksUrl: String,
        callback: (Boolean, String) -> Unit,
    ) {
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            try {
                val response = client.post(registerUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        RegisterBody(
                            oidcToken = oidcToken,
                            providerToken = providerToken,
                            registerToken = registerToken,
                            oidcJwksUrl = oidcJwksUrl,
                            providerJwksUrl = providerJwksUrl,
                        ),
                    )
                }
                val body = response.body<KicpApiResponse>()
                if (response.status.isSuccess() && body.success) {
                    callback(true, body.identityId ?: "登録が完了しました")
                } else {
                    callback(false, body.message ?: "登録に失敗しました")
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "不明なエラー")
            }
        }
    }

    /**
     * 登録トークンを取得する（idServerA側）
     *
     * @param serverUrl サーバーのベースURL
     * @param oidcToken OIDCトークン
     * @param providerToken プロバイダートークン
     * @param oidcJwksUrl OIDC JWKSのURL
     * @param providerJwksUrl プロバイダー JWKSのURL
     * @param callback 結果を受け取るコールバック (success: Boolean, registerToken: String)
     */
    @JsName("getRegisterToken")
    fun getRegisterToken(
        serverUrl: String,
        oidcToken: String,
        providerToken: String,
        oidcJwksUrl: String,
        providerJwksUrl: String,
        callback: (Boolean, String) -> Unit,
    ) {
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            try {
                val response = client.post("$serverUrl/api/kicp/get-register-token") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        GetRegisterTokenBody(
                            oidcToken = oidcToken,
                            providerToken = providerToken,
                            oidcJwksUrl = oidcJwksUrl,
                            providerJwksUrl = providerJwksUrl,
                        ),
                    )
                }
                val body = response.body<KicpApiResponse>()
                if (response.status.isSuccess() && body.success) {
                    callback(true, body.registerToken ?: "")
                } else {
                    callback(false, body.message ?: "登録トークンの取得に失敗しました")
                }
            } catch (e: Exception) {
                callback(false, e.message ?: "不明なエラー")
            }
        }
    }
}
