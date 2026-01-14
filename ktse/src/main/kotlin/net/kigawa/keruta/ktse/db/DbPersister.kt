package net.kigawa.keruta.ktse.db

import net.kigawa.keruta.ktse.KtseConfig

class DbPersister(
    ktseConfig: KtseConfig
) {
    init {
        FlywayMigrator().migrate(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
    }
}
