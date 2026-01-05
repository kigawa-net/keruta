package net.kigawa.keruta.ktse

import io.ktor.server.application.ApplicationEnvironment

class Config(environment: ApplicationEnvironment) {
    val issuer = environment.config.property("ktor.security.keycloak.issuer").getString()
    val audience = environment.config.property("ktor.security.keycloak.clientId").getString()
    val realm = environment.config.property("ktor.security.keycloak.realm").getString()
}
