package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import net.kigawa.keruta.ktcp.server.auth.VerifyConfig

class KtseConfig(environment: ApplicationEnvironment) {

    val zkHost = environment.config.property("zk.host").getString()
    val verifyConfig = Verify(environment)
    val dbConfig = Database(environment)

    class Verify(environment: ApplicationEnvironment): VerifyConfig {
        override val issuer: String = environment.config.property("ktor.security.keycloak.issuer").getString()
        override val jwksUrl: String = "${issuer}/protocol/openid-connect/certs"
        override val audience: String = environment.config.property("ktor.security.keycloak.clientId").getString()

    }

    class Database(environment: ApplicationEnvironment) {
        val jdbcUrl: String = environment.config.property("db.jdbcUrl").getString()
        val username: String = environment.config.property("db.username").getString()
        val password: String = environment.config.property("db.password").getString()
    }
}
