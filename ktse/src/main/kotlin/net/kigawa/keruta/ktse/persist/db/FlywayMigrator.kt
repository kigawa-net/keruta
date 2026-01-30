package net.kigawa.keruta.ktse.persist.db

import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.log.traceignore.error
import org.flywaydb.core.Flyway

class FlywayMigrator {
    private val logger = getKogger()

    fun migrate(
        jdbcUrl: String,
        username: String,
        password: String,
    ) {
        logger.info("Starting Flyway migration")
        logger.debug("JDBC URL: $jdbcUrl")

        try {
            val flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .failOnMissingLocations(false)
                .load()

            val result = flyway.migrate()
            logger.info("Flyway migration completed: ${result.migrationsExecuted} migrations executed")
        } catch (e: Exception) {
            logger.error("Flyway migration failed", e)
            throw e
        }
    }
}
