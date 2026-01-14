package net.kigawa.keruta.ktse.db

import net.kigawa.keruta.ktse.KtseConfig
import org.jetbrains.exposed.sql.Database

class DbPersister(
    ktseConfig: KtseConfig,
) {
    private val db: Database = Database.connect(ktseConfig.dbConfig.jdbcUrl, driver = "com.mysql.cj.jdbc.Driver")

    init {
        FlywayMigrator().migrate(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
    }
}
