package net.kigawa.keruta.ktse.persist.db

import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.log.traceignore.error
import net.kigawa.kodel.api.log.traceignore.warn
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo

class FlywayMigrator {
    private val logger = getKogger()

    private fun createFlyway(
        jdbcUrl: String,
        username: String,
        password: String,
    ): Flyway {
        val classLoader = Thread.currentThread().contextClassLoader
        logger.debug("Using ClassLoader: ${classLoader.javaClass.name}")

        return Flyway.configure(classLoader)
            .dataSource(jdbcUrl, username, password)
            .locations("classpath:db/migration")
            .createSchemas(true)
            .load()
    }

    fun migrate(
        jdbcUrl: String,
        username: String,
        password: String,
    ) {
        logger.info("Starting Flyway migration")
        logger.debug("JDBC URL: $jdbcUrl")

        try {
            val flyway = createFlyway(jdbcUrl, username, password)

            // Log migration info before migration
            logger.info("Checking available migrations...")
            val info = flyway.info()
            logger.info("Found ${info.all().size} migrations:")
            info.all().forEach { migration ->
                logger.info("  - ${migration.version}: ${migration.description} [${migration.state}]")
            }

            val result = flyway.migrate()
            logger.info("Flyway migration completed: ${result.migrationsExecuted} migrations executed")
        } catch (e: Exception) {
            logger.error("Flyway migration failed", e)
            throw e
        }
    }

    fun rollbackToVersion(
        jdbcUrl: String,
        username: String,
        password: String,
        targetVersion: String,
    ) {
        logger.info("Starting Flyway rollback to version: $targetVersion")
        logger.debug("JDBC URL: $jdbcUrl")

        try {
            val flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .failOnMissingLocations(false)
                .target(org.flywaydb.core.api.MigrationVersion.fromVersion(targetVersion))
                .load()

            logger.info("WARNING: Rolling back to version $targetVersion - this will clean the database!")

            // データベースをクリーン
            val cleanResult = flyway.clean()
            logger.info("Database cleaned: ${cleanResult.schemasCleaned.size} schemas cleaned")

            // 指定バージョンまでマイグレート
            val migrateResult = flyway.migrate()
            logger.info("Rollback completed: ${migrateResult.migrationsExecuted} migrations executed to version $targetVersion")
        } catch (e: Exception) {
            logger.error("Flyway rollback failed", e)
            throw e
        }
    }

    fun clean(
        jdbcUrl: String,
        username: String,
        password: String,
    ) {
        logger.info("Starting Flyway clean")
        logger.debug("JDBC URL: $jdbcUrl")

        try {
            val flyway = createFlyway(jdbcUrl, username, password)
            logger.warn("Cleaning database - all schema objects will be deleted!")

            val result = flyway.clean()
            logger.info("Database cleaned: ${result.schemasCleaned.size} schemas cleaned")
        } catch (e: Exception) {
            logger.error("Flyway clean failed", e)
            throw e
        }
    }

    fun info(
        jdbcUrl: String,
        username: String,
        password: String,
    ): Array<MigrationInfo> {
        logger.info("Getting Flyway migration info")
        logger.debug("JDBC URL: $jdbcUrl")

        try {
            val flyway = createFlyway(jdbcUrl, username, password)
            val info = flyway.info()

            logger.info("Current migration state:")
            info.all().forEach { migration ->
                logger.info("  - ${migration.version}: ${migration.description} [${migration.state}]")
            }

            return info.all()
        } catch (e: Exception) {
            logger.error("Failed to get Flyway info", e)
            throw e
        }
    }

    fun repair(
        jdbcUrl: String,
        username: String,
        password: String,
    ) {
        logger.info("Starting Flyway repair")
        logger.debug("JDBC URL: $jdbcUrl")

        try {
            val flyway = createFlyway(jdbcUrl, username, password)
            val result = flyway.repair()

            logger.info("Repair completed: ${result.repairActions.size} actions performed")
            result.repairActions.forEach { action ->
                logger.info("  - $action")
            }
        } catch (e: Exception) {
            logger.error("Flyway repair failed", e)
            throw e
        }
    }
}
