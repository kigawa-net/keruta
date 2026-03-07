package net.kigawa.keruta.ktcl.k8s.login

import com.auth0.jwt.JWT
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcl.k8s.config.KtseConfig
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.keruta.ktcp.domain.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.usecase.client.ProviderTokenCreator
import net.kigawa.kodel.api.log.getKogger
import kotlin.time.Duration.Companion.seconds

class ProviderRegistrationClient(
    private val ktseConfig: KtseConfig,
    private val providerTokenCreator: ProviderTokenCreator,
) {
    private val logger = getKogger()
    private val serializer = JsonKerutaSerializer()

    suspend fun register(
        userToken: String,
        oidcSession: OidcSession,
        registerToken: String,
    ) {
        val serverToken = try {
            providerTokenCreator.create(JWT.decode(userToken).subject)
        } catch (e: Exception) {
            logger.severe("Failed to get OIDC tokens for ktse provider registration: ${e.message}")
            return
        }

        val client = HttpClient(CIO) {
            install(WebSockets)
        }

        try {
            client.use { httpClient ->
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = ktseConfig.host,
                    port = ktseConfig.port,
                    path = "/ws/ktcp",
                ) {
                    send(
                        serializer.serialize(
                            ServerProviderCompleteMsg(
                                registerToken = registerToken,
                                userToken = userToken,
                                serverToken = serverToken.createdToken.rawToken,
                                userAudience = oidcSession.clientId,
                                providerAudience = ktseConfig.providerAudience,
                                providerName = "keruta-k8s",
                            )
                        )
                    )

                    logger.info("Provider registration sent to ktse")
                    withTimeout(30.seconds) {
                        for (frame in incoming) {
                            if (frame !is Frame.Text) continue
                            val type = parseType(frame.readText()) ?: continue
                            if (type == ClientMsgType.PROVIDER_IDP_ADDED.str) break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.severe("Failed to register provider in ktse: ${e.message}")
        }
    }


    private fun parseType(text: String): String? {
        val jsonElement = Json.parseToJsonElement(text)
        return (jsonElement as? JsonObject)?.get("type")?.jsonPrimitive?.content
    }
}
