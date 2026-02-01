package net.kigawa.keruta.ktse.persist.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.persist.db.dsl.DbPersisterDsl
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

class DbPersister(
    ktseConfig: KtseConfig,
) {

    fun <R> execTransaction(block: (DbPersisterDsl) -> R): R {
        return transaction(db = db) {
            val dsl = DbPersisterDsl(this@transaction)
            val res = block(dsl)
            res
        }
    }

    private val config = HikariConfig().apply {
        jdbcUrl = ktseConfig.dbConfig.jdbcUrl + "?useInformationSchema=false"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = ktseConfig.dbConfig.username
        password = ktseConfig.dbConfig.password
        maximumPoolSize = 10
    }
    val db: Database = Database.connect(HikariDataSource(config))

    init {
        val migrator = FlywayMigrator()
        // Repair any failed migrations before attempting to migrate
        migrator.repair(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
        migrator.migrate(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
    }
}
