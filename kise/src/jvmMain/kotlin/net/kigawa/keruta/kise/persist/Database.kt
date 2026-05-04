package net.kigawa.keruta.kise.persist

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.kigawa.keruta.kise.KiseConfig
import org.jetbrains.exposed.v1.jdbc.Database

/**
 * データベース接続管理
 */
object KiseDatabase {
    private var dataSource: HikariDataSource? = null

    fun connect(config: KiseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.databaseUrl
            username = config.databaseUser
            password = config.databasePassword
            driverClassName = "com.mysql.cj.jdbc.Driver"
            maximumPoolSize = 10
            minimumIdle = 2
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000
        }

        dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource!!)
    }

    fun close() {
        dataSource?.close()
    }
}