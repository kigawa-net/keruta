package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import net.kigawa.keruta.ktcp.server.auth.IdpConfig
import net.kigawa.keruta.ktcp.server.auth.ProviderIdpConfig

class KtseConfig(environment: ApplicationEnvironment) {

    val zkHost = environment.config.property("zk.host").getString()
    val defaultIdp = environment.config.configList("ktor.security.defaultIdp").map {
        IdpConfig(
            it.property("issuer").getString(),
            it.property("audience").getString()
        )
    }
    val defaultProvider = environment.config.configList("ktor.security.defaultProvider").map {
        ProviderIdpConfig(
            it.property("issuer").getString(),
            it.property("audience").getString(),
            it.property("name").getString(),
        )
    }

    val dbConfig = Database(environment)

    class Database(environment: ApplicationEnvironment) {
        val jdbcUrl: String = environment.config.property("db.jdbcUrl").getString()
        val username: String = environment.config.property("db.username").getString()
        val password: String = environment.config.property("db.password").getString()
    }
}
