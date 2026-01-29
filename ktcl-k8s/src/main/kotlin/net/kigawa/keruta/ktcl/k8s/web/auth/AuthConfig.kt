package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession
import net.kigawa.kodel.api.log.Kogger
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

private val logger = LoggerFactory.get("AuthConfig")

data class KeycloakConfig(
    val url: String,
    val realm: String,
    val clientId: String,
    val jwksUrl: String,
    val issuer: String
)

fun Application.configureAuth() {
    val keycloakConfig = getKeycloakConfig()
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

private fun Application.getKeycloakConfig(): KeycloakConfig {
    val config = environment.config
    val url = config.property("keycloak.url").getString()
    val realm = config.property("keycloak.realm").getString()
    val clientId = config.property("keycloak.clientId").getString()
    val jwksUrl = config.property("keycloak.jwksUrl").getString()
    val issuer = config.property("keycloak.issuer").getString()

    return KeycloakConfig(url, realm, clientId, jwksUrl, issuer)
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
            .withAudience(keycloakConfig.clientId)
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
