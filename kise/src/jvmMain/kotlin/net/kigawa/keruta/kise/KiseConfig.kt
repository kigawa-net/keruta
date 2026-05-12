package net.kigawa.keruta.kise

import io.ktor.server.application.*

/**
 * Kise 設定
 */
class KiseConfig(
    environment: ApplicationEnvironment,
) {
    val issuer: String = environment.config.propertyOrNull("kise.issuer")?.getString()
        ?: "https://id.kigawa.net"

    val audience: String = environment.config.propertyOrNull("kise.audience")?.getString()
        ?: "keruta"

    val tokenExpiresInMs: Long = environment.config.propertyOrNull("kise.token.expiresInMs")?.getString()?.toLongOrNull()
        ?: 3_600_000

    val sessionTimeoutMs: Long = environment.config.propertyOrNull("kise.session.timeoutMs")?.getString()?.toLongOrNull()
        ?: 3_600_000

    val maxSessionsPerUser: Int = environment.config.propertyOrNull("kise.session.maxPerUser")?.getString()?.toIntOrNull()
        ?: 10

    val databaseUrl: String = environment.config.propertyOrNull("kise.database.url")?.getString()
        ?: "jdbc:mysql://localhost:3306/keruta"

    val databaseUser: String = environment.config.propertyOrNull("kise.database.user")?.getString()
        ?: "keruta"

    val databasePassword: String = environment.config.propertyOrNull("kise.database.password")?.getString()
        ?: ""

    val defaultUserIdpIssuer: String = environment.config.propertyOrNull("kise.default.userIdp.issuer")?.getString()
        ?: "https://example.auth0.com/"

    val defaultProviderIssuer: String = environment.config.propertyOrNull("kise.default.provider.issuer")?.getString()
        ?: "https://example.auth0.com/"

    val jwtPublicKey: String = environment.config.propertyOrNull("kise.jwt.publicKey")?.getString()
        ?: ""

    val jwtPrivateKey: String = environment.config.propertyOrNull("kise.jwt.privateKey")?.getString()
        ?: ""

    // OIDC設定
    val oidcClientId: String = environment.config.propertyOrNull("kise.oidc.clientId")?.getString()
        ?: "kise-client"

    val oidcRedirectUri: String = environment.config.propertyOrNull("kise.oidc.redirectUri")?.getString()
        ?: "http://localhost:8080/callback"

    // KICP設定（idServerBとして機能する際のpeerServerA URL）
    val kicpPeerServerBaseUrl: String = environment.config.propertyOrNull("kise.kicp.peerServerBaseUrl")?.getString()
        ?: "http://localhost:8080"
}
