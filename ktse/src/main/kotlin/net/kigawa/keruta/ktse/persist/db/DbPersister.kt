package net.kigawa.keruta.ktse.persist.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.kigawa.keruta.ktcp.server.auth.VerifyTablesPersister
import net.kigawa.keruta.ktse.KtseConfig
import net.kigawa.keruta.ktse.persist.accessor.ExposedVerifyTablesPersister
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

    val verifyTablesPersister: VerifyTablesPersister = ExposedVerifyTablesPersister(this)
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
        // 直接 migrate を実行（repair は Flyway 9.x で問題を起こす可能性があるためスキップ）
        migrator.migrate(
            jdbcUrl = ktseConfig.dbConfig.jdbcUrl,
            username = ktseConfig.dbConfig.username,
            password = ktseConfig.dbConfig.password,
        )
    }
}
