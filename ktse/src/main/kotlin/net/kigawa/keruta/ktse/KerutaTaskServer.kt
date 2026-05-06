package net.kigawa.keruta.ktse

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktse.websocket.KtorWebsocketModule
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.handler.StdHandler

@Suppress("unused")
class KerutaTaskServer {

    init {
        LoggerFactory.configure {
            level = LogLevel.INFO
            handler(::StdHandler) {
                level = LogLevel.DEBUG
            }

            child("net.kigawa") {
                child("net/kigawa/keruta") {
                    level = LogLevel.DEBUG
                }
                child("kodel") {
//                        level = LogLevel.DEBUG
                }
            }
        }
    }


    @Suppress("unused")
    fun Application.module() {
        val ws = KtorWebsocketModule(this@module,this@KerutaTaskServer)
        routing {
            ws.websocketModule(this@routing)

            // kicpクライアント登録エンドポイント
            post("/api/kicp/register") {
                try {
                    val params = call.receive<Map<String, String>>()
                    val oidcToken = params["oidcToken"] ?: ""
                    val providerToken = params["providerToken"] ?: ""
                    val registerToken = params["registerToken"] ?: ""

                    // TODO: 実際のkicp登録ロジックを実装する
                    // 現在はモックレスポンスを返す
                    val logMsg = "kicp登録リクエスト受信: oidcToken=" + oidcToken.take(20) + "..."
                    call.application.environment.log.info(logMsg)

                    call.respond(
                        HttpStatusCode.OK,
                        mapOf(
                            "success" to true,
                            "message" to "kicpクライアント登録リクエストを受け付けました（モック）",
                            "received" to mapOf(
                                "hasOidcToken" to oidcToken.isNotEmpty(),
                                "hasProviderToken" to providerToken.isNotEmpty(),
                                "hasRegisterToken" to registerToken.isNotEmpty()
                            )
                        )
                    )
                } catch (e: Exception) {
                    call.application.environment.log.error("kicp登録エラー: " + (e.message ?: ""))
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("success" to false, "message" to ("エラー: " + (e.message ?: "")))
                    )
                }
            }
        }
    }
}
