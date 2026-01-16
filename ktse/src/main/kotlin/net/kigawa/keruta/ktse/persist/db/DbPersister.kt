package net.kigawa.keruta.ktse.persist.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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

    private val config = HikariConfig().apply {
        jdbcUrl = ktseConfig.dbConfig.jdbcUrl + "?useInformationSchema=false"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = ktseConfig.dbConfig.username
        password = ktseConfig.dbConfig.password
        maximumPoolSize = 10
    }
    private val db: Database = Database.connect(HikariDataSource(config))

    init {
        FlywayMigrator().migrate(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
    }
}
