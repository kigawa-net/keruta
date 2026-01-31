package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import kotlinx.coroutines.runBlocking
import net.kigawa.kodel.api.log.LoggerFactory
import java.net.URL
import java.util.concurrent.TimeUnit


class AuthConfig {
    private val logger = LoggerFactory.get("AuthConfig")

    fun loadKeycloakConfig(issuer: String, audience: String): KeycloakConfig {
        val wellKnownUrl = "$issuer/.well-known/openid-configuration"
        logger.info("Fetching OIDC configuration from: $wellKnownUrl")

        val oidcMetadata = runBlocking {
            OidcDiscoveryFetcher().fetch(wellKnownUrl)
        }

        logger.info("OIDC configuration fetched successfully. JWKS URI: ${oidcMetadata.jwksUri}")

        return KeycloakConfig(
            audience = audience,
            jwksUrl = oidcMetadata.jwksUri,
            issuer = issuer
        )
    }

    fun createJwkProvider(jwksUrl: String): JwkProvider {
        @Suppress("DEPRECATION")
        return JwkProviderBuilder(URL(jwksUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
    }
}


