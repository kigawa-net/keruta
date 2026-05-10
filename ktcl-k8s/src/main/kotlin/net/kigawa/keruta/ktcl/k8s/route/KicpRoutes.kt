package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.token.OidcToken
import net.kigawa.keruta.kicp.domain.token.ProviderToken
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.keruta.kicp.usecase.register.RegisterInput
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCase
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

@Serializable
data class KicpRegisterRequest(
    val oidcToken: String,
    val providerToken: String,
    val registerToken: String,
    val oidcJwksUrl: String,
    val providerJwksUrl: String,
)

/** KICPのidServerB側エンドポイント */
class KicpRoutes(
    private val registerUseCase: RegisterUseCase,
) {
    private val logger = LoggerFactory.get("KicpRoutes")

    fun configure(route: Route) = route.route("/api/kicp") {
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
                            mapOf(
                                "success" to false,
                                "message" to "登録に失敗しました: ${result.err.message}",
                            ),
                        )
                    }
                }
            } catch (e: Exception) {
                logger.severe("KICP登録エラー: ${e.message}")
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("success" to false, "message" to "エラー: ${e.message}"),
                )
            }
        }
    }
}
