package net.kigawa.keruta.ktse

import io.ktor.server.application.*

class KtseConfig(environment: ApplicationEnvironment) {

    val issuer = environment.config.property("ktor.security.keycloak.issuer").getString()
    val audience = environment.config.property("ktor.security.keycloak.clientId").getString()
    val realm = environment.config.property("ktor.security.keycloak.realm").getString()
    val zkHost = environment.config.property("zk.host").getString()
}
