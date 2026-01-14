package net.kigawa.keruta.ktse.persist.db

import net.kigawa.keruta.ktse.KtseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

class DbPersister(
    ktseConfig: KtseConfig,
) {

    fun <R> execTransaction(block: DbPersisterDSL.() -> R): R {
        return transaction(db = db) {
            val dsl = DbPersisterDSL(this@transaction)
            val res = dsl.block()
            res
        }
    }

    private val db: Database = Database.connect(ktseConfig.dbConfig.jdbcUrl, driver = "com.mysql.cj.jdbc.Driver")

    init {
        FlywayMigrator().migrate(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
    }
}
