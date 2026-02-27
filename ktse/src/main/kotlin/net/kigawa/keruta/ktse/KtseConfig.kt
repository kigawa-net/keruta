package net.kigawa.keruta.ktse

import io.ktor.server.application.*
import net.kigawa.keruta.ktcp.server.auth.UserIdpConfig
import net.kigawa.keruta.ktcp.server.auth.ProviderIdpConfig
import net.kigawa.kodel.api.net.Url

class KtseConfig(environment: ApplicationEnvironment) {

    val zkHost = environment.config.property("zk.host").getString()
    val defaultUserIdp = UserIdpConfig(
        environment.config.property("ktor.security.defaultIdp.issuer").getString()
            .let { Url.parse(it) },
        environment.config.property("ktor.security.defaultIdp.audience").getString()
    )

    val defaultProviderIdp = ProviderIdpConfig(
        environment.config.property("ktor.security.defaultProvider.issuer").getString().let { Url.parse(it) },
        environment.config.property("ktor.security.defaultProvider.audience").getString(),
        environment.config.property("ktor.security.defaultProvider.name").getString(),
    )


    val jwtSecret: String =
        environment.config.propertyOrNull("ktor.security.jwtSecret")?.getString()
            ?: System.getenv("KTSE_JWT_SECRET")
            ?: throw IllegalStateException("KTSE_JWT_SECRET not set")

    val dbConfig = Database(environment)

    class Database(environment: ApplicationEnvironment) {
        val jdbcUrl: String = environment.config.property("db.jdbcUrl").getString()
        val username: String = environment.config.property("db.username").getString()
        val password: String = environment.config.property("db.password").getString()
    }
}
