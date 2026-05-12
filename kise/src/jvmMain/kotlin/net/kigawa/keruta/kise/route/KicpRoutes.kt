package net.kigawa.keruta.kise.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.token.OidcToken
import net.kigawa.keruta.kicp.domain.token.ProviderToken
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.keruta.kicp.usecase.login.LoginInput
import net.kigawa.keruta.kicp.usecase.login.LoginUseCase
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenInput
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.RegisterInput
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCase
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterInput
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCase
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

@Serializable
data class KicpGetRegisterTokenRequest(
    val oidcToken: String,
    val providerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)

@Serializable
data class KicpRegisterRequest(
    val oidcToken: String,
    val providerToken: String,
    val registerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)

@Serializable
data class KicpVerifyRegisterRequest(
    val registerId: String,
    val registerToken: String,
)

/**
 * KICPのidServer（A/B両方）エンドポイント
 */
class KicpRoutes(
    private val loginUseCase: LoginUseCase,
    private val getRegisterTokenUseCase: GetRegisterTokenUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyRegisterTokenUseCase: VerifyRegisterTokenUseCase,
) {
    private val logger = LoggerFactory.get("KicpRoutes")

    fun configure(route: Route) = route.route("/api/kicp") {
        // idServerA: 登録トークン発行
        post("/get-register-token") {
            try {
                val request = call.receive<KicpGetRegisterTokenRequest>()

                val loginInput = LoginInput(
                    oidcToken = OidcToken(request.oidcToken),
                    oidcJwksUrl = JwksUrl(request.oidcJwksUrl),
                    providerToken = ProviderToken(request.providerToken),
                    providerJwksUrl = JwksUrl(request.providerJwksUrl),
                )
                val identityId = when (val result = loginUseCase.login(loginInput)) {
                    is Res.Err -> {
                        logger.warning("KICP認証失敗: ${result.err.message}")
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("success" to false, "message" to "認証に失敗しました: ${result.err.message}"),
                        )
                        return@post
                    }
                    is Res.Ok -> result.value
                }

                when (val result = getRegisterTokenUseCase.getRegisterToken(GetRegisterTokenInput(identityId))) {
                    is Res.Ok -> {
                        logger.info("KICP登録トークン発行: identityId=${identityId.value}")
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("success" to true, "registerToken" to result.value.value),
                        )
                    }
                    is Res.Err -> {
                        logger.warning("KICP登録トークン発行失敗: ${result.err.message}")
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("success" to false, "message" to "登録トークンの発行に失敗しました: ${result.err.message}"),
                        )
                    }
                }
            } catch (e: Exception) {
                logger.severe("KICP get-register-token エラー: ${e.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("success" to false, "message" to "エラー: ${e.message}"),
                )
            }
        }

        // idServerB: 登録フロー
        post("/register") {
            try {
                val request = call.receive<KicpRegisterRequest>()

                val input = RegisterInput(
                    oidcToken = OidcToken(request.oidcToken),
                    oidcJwksUrl = JwksUrl(request.oidcJwksUrl),
                    providerToken = ProviderToken(request.providerToken),
                    providerJwksUrl = JwksUrl(request.providerJwksUrl),
                    registerToken = RegisterToken(request.registerToken),
                )

                when (val result = registerUseCase.register(input)) {
                    is Res.Ok -> {
                        logger.info("KICP登録完了: identityId=${result.value.value}")
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf(
                                "success" to true,
                                "message" to "登録が完了しました",
                                "identityId" to result.value.value,
                            ),
                        )
                    }
                    is Res.Err -> {
                        logger.warning("KICP登録失敗: ${result.err.message}")
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("success" to false, "message" to "登録に失敗しました: ${result.err.message}"),
                        )
                    }
                }
            } catch (e: Exception) {
                logger.severe("KICP register エラー: ${e.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("success" to false, "message" to "エラー: ${e.message}"),
                )
            }
        }

        // idServerA: 登録トークン検証
        post("/verify-register") {
            try {
                val request = call.receive<KicpVerifyRegisterRequest>()

                val input = VerifyRegisterInput(
                    registerId = RegisterId(request.registerId),
                    registerToken = RegisterToken(request.registerToken),
                    currentTimeMs = System.currentTimeMillis(),
                )

                when (val result = verifyRegisterTokenUseCase.verify(input)) {
                    is Res.Ok -> {
                        logger.info("KICP検証成功: identityId=${result.value.value}")
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf(
                                "success" to true,
                                "message" to "登録トークンの検証に成功しました",
                                "identityId" to result.value.value,
                            ),
                        )
                    }
                    is Res.Err -> {
                        logger.warning("KICP検証失敗: ${result.err.message}")
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("success" to false, "message" to "検証に失敗しました: ${result.err.message}"),
                        )
                    }
                }
            } catch (e: Exception) {
                logger.severe("KICP verify-register エラー: ${e.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("success" to false, "message" to "エラー: ${e.message}"),
                )
            }
        }
    }
}
