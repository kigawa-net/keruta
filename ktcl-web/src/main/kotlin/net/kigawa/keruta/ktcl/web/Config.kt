package net.kigawa.keruta.ktcl.web

import io.ktor.server.config.*

data class Config(
    val audience: String,
    val issuer: String,
    val realm: String,
    val clientId: String,
    val clientSecret: String
) {
    companion object {
        fun load(config: ApplicationConfig): Config {
            val keycloakConfig = config.config("ktor.security.keycloak")
            return Config(
                audience = keycloakConfig.property("audience").getString(),
                issuer = keycloakConfig.property("issuer").getString(),
                realm = keycloakConfig.property("realm").getString(),
                clientId = keycloakConfig.property("clientId").getString(),
                clientSecret = keycloakConfig.property("clientSecret").getString()
            )
        }
    }
}
