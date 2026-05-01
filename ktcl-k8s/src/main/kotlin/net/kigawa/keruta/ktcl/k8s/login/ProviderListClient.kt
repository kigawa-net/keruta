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
import net.kigawa.keruta.ktcl.k8s.dto.ProviderDto
import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.domain.provider.list.ServerProviderListMsg
import net.kigawa.keruta.ktcp.domain.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.domain.serialize.deserialize
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.usecase.client.ProviderTokenCreator
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import kotlin.time.Duration.Companion.seconds

class ProviderListClient(
    private val ktseConfig: KtseConfig,
    private val providerTokenCreator: ProviderTokenCreator,
) {
    private val logger = getKogger()
    private val serializer = JsonKerutaSerializer()

    suspend fun listProviders(userToken: String): List<ProviderDto> {
        val serverToken = try {
            providerTokenCreator.create(JWT.decode(userToken).subject)
        } catch (e: Exception) {
            logger.severe("Failed to create server token: ${e.message}")
            return emptyList()
        }

        var providers: List<ProviderDto> = emptyList()

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
                            ServerAuthRequestMsg(
                                userToken = userToken,
                                serverToken = serverToken.createdToken.rawToken,
                            )
                        )
                    )

                    withTimeout(10.seconds) {
                        for (frame in incoming) {
                            if (frame !is Frame.Text) continue
                            val type = parseType(frame.readText()) ?: continue
                            if (type == ServerMsgType.AUTH_SUCCESS.str) break
                        }
                    }

                    send(serializer.serialize(ServerProviderListMsg()))

                    withTimeout(10.seconds) {
                        for (frame in incoming) {
                            if (frame !is Frame.Text) continue
                            val text = frame.readText()
                            val type = parseType(text) ?: continue
                            if (type != ClientMsgType.PROVIDER_LISTED.str) continue
                            val res = serializer.deserialize<ClientProviderListedMsg>(text)
                            when (res) {
                                is Res.Ok -> providers = res.value.providers.map { p ->
                                    ProviderDto(id = p.id, name = p.name, issuer = p.issuer, audience = p.audience)
                                }
                                is Res.Err -> logger.severe("Failed to deserialize provider_listed: ${res.err}")
                            }
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.severe("Failed to list providers: ${e.message}")
        }

        return providers
    }

    private fun parseType(text: String): String? {
        val jsonElement = Json.parseToJsonElement(text)
        return (jsonElement as? JsonObject)?.get("type")?.jsonPrimitive?.content
    }
}
