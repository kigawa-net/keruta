package net.kigawa.keruta.ktse

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import net.kigawa.keruta.ktse.kicp.RegisterRequest
import net.kigawa.keruta.ktse.kicp.RegisterUseCaseFactory
import net.kigawa.keruta.ktse.kicp.VerifyRegisterRequest
import net.kigawa.keruta.ktse.kicp.VerifyRegisterTokenUseCaseFactory
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterInput
import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.token.RegisterToken
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
        install(ContentNegotiation) {
            json()
        }
        val ws = KtorWebsocketModule(this@module,this@KerutaTaskServer)
        routing {
            ws.websocketModule(this@routing)

            // kicpクライアント登録エンドポイント
            post("/api/kicp/register") {
                try {
                    val request = call.receive<RegisterRequest>()
                    
                    val registerUseCase = RegisterUseCaseFactory.create()
                    val input = net.kigawa.keruta.kicp.usecase.register.RegisterInput(
                        oidcToken = net.kigawa.keruta.kicp.domain.token.OidcToken(request.oidcToken),
                        oidcJwksUrl = net.kigawa.keruta.kicp.domain.jwks.JwksUrl(request.oidcJwksUrl),
                        providerToken = net.kigawa.keruta.kicp.domain.token.ProviderToken(request.providerToken),
                        providerJwksUrl = net.kigawa.keruta.kicp.domain.jwks.JwksUrl(request.providerJwksUrl),
                        registerToken = net.kigawa.keruta.kicp.domain.token.RegisterToken(request.registerToken),
                    )
                    
                     when (val result = registerUseCase.register(input)) {
                         is net.kigawa.kodel.api.err.Res.Ok -> {
                             call.respond(
                                 HttpStatusCode.OK,
                                 mapOf(
                                     "success" to true,
                                     "message" to "kicpクライアント登録が完了しました",
                                     "identityId" to result.value.value
                                 )
                             )
                         }
                         is net.kigawa.kodel.api.err.Res.Err -> {
                             call.application.environment.log.error("kicp登録失敗: ${result.err.message}")
                             call.respond(
                                 HttpStatusCode.BadRequest,
                                 mapOf(
                                     "success" to false,
                                     "message" to "登録に失敗しました: ${result.err.message}"
                                 )
                             )
                         }
                     }
                } catch (e: Exception) {
                    call.application.environment.log.error("kicp登録エラー: " + (e.message ?: ""))
                     call.respond(
                         HttpStatusCode.InternalServerError,
                         mapOf("success" to false, "message" to ("エラー: " + (e.message ?: "")))
                     )
                }
            }
            
            // kicp登録トークン検証エンドポイント（idServerA側）
            post("/api/kicp/verify-register") {
                try {
                    val request = call.receive<VerifyRegisterRequest>()
                    
                    val verifyUseCase = VerifyRegisterTokenUseCaseFactory.create()
                    val input = VerifyRegisterInput(
                        registerId = RegisterId(request.registerId),
                        registerToken = RegisterToken(request.registerToken),
                        currentTimeMs = System.currentTimeMillis(),
                    )
                    
                    when (val result = verifyUseCase.verify(input)) {
                        is net.kigawa.kodel.api.err.Res.Ok -> {
                            call.respond(
                                HttpStatusCode.OK,
                                mapOf(
                                    "success" to true,
                                    "message" to "登録トークンの検証に成功しました",
                                    "identityId" to result.value.value
                                )
                            )
                        }
                        is net.kigawa.kodel.api.err.Res.Err -> {
                            call.application.environment.log.error("kicp検証失敗: ${result.err.message}")
                            call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf(
                                    "success" to false,
                                    "message" to "検証に失敗しました: ${result.err.message}"
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    call.application.environment.log.error("kicp検証エラー: " + (e.message ?: ""))
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("success" to false, "message" to ("エラー: " + (e.message ?: "")))
                    )
                }
            }
        }
    }
}
