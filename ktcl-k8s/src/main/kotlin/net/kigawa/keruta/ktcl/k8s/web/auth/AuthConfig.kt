package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import net.kigawa.keruta.ktcl.k8s.config.appConfig
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

private val logger = LoggerFactory.get("AuthConfig")

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class OidcDiscoveryResponse(
    @SerialName("issuer") val issuer: String,
    @SerialName("jwks_uri") val jwksUri: String,
    @SerialName("authorization_endpoint") val authorizationEndpoint: String? = null,
    @SerialName("token_endpoint") val tokenEndpoint: String? = null,
    @SerialName("userinfo_endpoint") val userinfoEndpoint: String? = null
)

data class KeycloakConfig(
    val audience: String,
    val jwksUrl: String,
    val issuer: String
)

fun Application.configureAuth() {
    val idpConfig = appConfig.idp
    val keycloakConfig = getKeycloakConfig(idpConfig.issuer, idpConfig.audience)
    logger.info("Configuring Keycloak authentication: ${keycloakConfig.issuer}")

    // JWKプロバイダー設定
    val jwkProvider = JwkProviderBuilder(URL(keycloakConfig.jwksUrl))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    // JWT検証関数を環境に設定
    environment.monitor.subscribe(ApplicationStarted) {
        attributes.put(JWK_PROVIDER_KEY, jwkProvider)
        attributes.put(KEYCLOAK_CONFIG_KEY, keycloakConfig)
    }
}

private fun getKeycloakConfig(issuer: String, audience: String): KeycloakConfig {
    // OIDC well-knownエンドポイントから動的に取得
    val wellKnownUrl = "$issuer/.well-known/openid-configuration"
    logger.info("Fetching OIDC configuration from: $wellKnownUrl")

    val oidcMetadata = runBlocking {
        fetchOidcMetadata(wellKnownUrl)
    }

    logger.info("OIDC configuration fetched successfully. JWKS URI: ${oidcMetadata.jwksUri}")

    return KeycloakConfig(
        audience = audience,
        jwksUrl = oidcMetadata.jwksUri,
        issuer = issuer
    )
}

private suspend fun fetchOidcMetadata(wellKnownUrl: String): OidcDiscoveryResponse {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    return try {
        client.get(wellKnownUrl).body<OidcDiscoveryResponse>()
    } catch (e: Exception) {
        logger.severe("Failed to fetch OIDC metadata from $wellKnownUrl: ${e.message}")
        throw RuntimeException("Failed to fetch OIDC metadata", e)
    } finally {
        client.close()
    }
}

val JWK_PROVIDER_KEY = AttributeKey<com.auth0.jwk.JwkProvider>("JwkProvider")
val KEYCLOAK_CONFIG_KEY = AttributeKey<KeycloakConfig>("KeycloakConfig")

/**
 * JWTトークンを検証する
 */
fun Application.verifyToken(token: String): String? {
    return try {
        val jwkProvider = attributes[JWK_PROVIDER_KEY]
        val keycloakConfig = attributes[KEYCLOAK_CONFIG_KEY]

        val jwt = JWT.decode(token)
        val jwk = jwkProvider.get(jwt.keyId)
        val publicKey = jwk.publicKey as RSAPublicKey
        val algorithm = Algorithm.RSA256(publicKey, null)

        val verifier = JWT.require(algorithm)
            .withIssuer(keycloakConfig.issuer)
            .withAudience(keycloakConfig.audience)
            .build()

        val decodedJwt = verifier.verify(token)
        decodedJwt.subject
    } catch (e: Exception) {
        logger.severe("JWT verification failed: ${e.message}")
        null
    }
}

/**
 * セッションから認証済みユーザーを取得する
 */
suspend fun ApplicationCall.getAuthenticatedUser(): UserSession? {
    val session = sessions.get<UserSession>()
    if (session == null) {
        logger.fine("No session found")
        return null
    }

    val userId = application.verifyToken(session.token)
    if (userId == null) {
        logger.fine("Token verification failed")
        sessions.clear<UserSession>()
        return null
    }

    return session
}

/**
 * 認証が必要なエンドポイント用の拡張関数
 */
suspend fun ApplicationCall.requireAuth(block: suspend (UserSession) -> Unit) {
    val user = getAuthenticatedUser()
    if (user == null) {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
        return
    }
    block(user)
}
