package net.kigawa.keruta.ktcl.k8s.config

import io.ktor.server.config.*

/**
 * データベース設定を保持するクラス
 */
data class DbConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
) {
    companion object {
        /**
         * ApplicationConfig と環境変数から設定を読み込む
         */
        fun load(applicationConfig: ApplicationConfig): DbConfig {
            return DbConfig(
                jdbcUrl = applicationConfig.property("db.jdbcUrl").getString(),
                username = applicationConfig.property("db.username").getString(),
                password = applicationConfig.property("db.password").getString()
            )
        }
    }
}
