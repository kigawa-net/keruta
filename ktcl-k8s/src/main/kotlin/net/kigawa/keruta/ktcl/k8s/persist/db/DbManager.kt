package net.kigawa.keruta.ktcl.k8s.persist.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.kigawa.keruta.ktcl.k8s.config.DbConfig
import org.jetbrains.exposed.sql.Database

/**
 * データベース接続を管理するクラス
 */
class DbManager(
    dbConfig: DbConfig,
) {
    private val config = HikariConfig().apply {
        jdbcUrl = dbConfig.jdbcUrl + "?useInformationSchema=false"
        driverClassName = "org.mariadb.jdbc.Driver"
        username = dbConfig.username
        password = dbConfig.password
        maximumPoolSize = 10
    }
    val db: Database = Database.connect(HikariDataSource(config))

    init {
        val migrator = FlywayMigrator()
        // Repair any failed migrations before attempting to migrate
        migrator.repair(
            jdbcUrl = dbConfig.jdbcUrl,
            username = dbConfig.username,
            password = dbConfig.password,
        )
        migrator.migrate(
            jdbcUrl = dbConfig.jdbcUrl,
            username = dbConfig.username,
            password = dbConfig.password,
        )
    }
}
