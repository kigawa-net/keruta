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
import net.kigawa.keruta.ktcp.base.auth.key.Auth0AlgorithmInitializer
import net.kigawa.keruta.ktcp.base.auth.key.JavaPrivateKeyInitializer
import net.kigawa.keruta.ktcp.model.auth.key.KerutaPrivateKey
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.kodel.api.log.getKogger
import java.util.*
import kotlin.time.Duration.Companion.seconds

class ProviderRegistrationClient(
    private val ktseConfig: KtseConfig,
    private val privateKey: KerutaPrivateKey,
    private val issuer: String,
    private val javaPrivateKeyInitializer: JavaPrivateKeyInitializer,
) {
    private val logger = getKogger()
    private val serializer = JsonKerutaSerializer()

    suspend fun register(
        registerToken: String,
        code: String,
        redirectUri: String,
        userToken: String,
    ) {
        val serverToken = try {
            createServerToken(userToken)
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
                    send(serializer.serialize(ServerAuthRequestMsg(
                        userToken = userToken,
                        serverToken = serverToken,
                    )))

                    withTimeout(30.seconds) {
                        for (frame in incoming) {
                            if (frame !is Frame.Text) continue
                            val type = parseType(frame.readText()) ?: continue
                            if (type == ServerMsgType.AUTH_SUCCESS.str) break
                        }
                    }

                    send(serializer.serialize(ServerProviderCompleteMsg(
                        token = registerToken,
                        code = code,
                        redirectUri = redirectUri,
                    )))

                    logger.info("Provider registration sent to ktse")
                }
            }
        } catch (e: Exception) {
            logger.severe("Failed to register provider in ktse: ${e.message}")
        }
    }

    private fun createServerToken(userToken: String): String {
        val subject = JWT.decode(userToken).subject
        val key = javaPrivateKeyInitializer.initialize(privateKey)
        val algorithm = Auth0AlgorithmInitializer().initPrivateKey(key)
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(ktseConfig.providerAudience)
            .withSubject(subject)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
            .sign(algorithm)
    }

    private fun parseType(text: String): String? {
        val jsonElement = Json.parseToJsonElement(text)
        return (jsonElement as? JsonObject)?.get("type")?.jsonPrimitive?.content
    }
}
