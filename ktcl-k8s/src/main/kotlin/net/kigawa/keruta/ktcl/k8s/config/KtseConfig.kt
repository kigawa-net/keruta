package net.kigawa.keruta.ktcl.k8s.config

/**
 * KTSE接続設定
 */
data class KtseConfig(
    val host: String,
    val port: Int,
    val useTls: Boolean,
    val userToken: String,
    val serverToken: String,
    val queueId: Long
) {
    companion object {
        fun fromEnvironment(): KtseConfig {
            return KtseConfig(
                host = System.getenv("KTSE_HOST") ?: "localhost",
                port = System.getenv("KTSE_PORT")?.toInt() ?: 8080,
                useTls = System.getenv("KTSE_USE_TLS")?.toBoolean() ?: false,
                userToken = System.getenv("KERUTA_USER_TOKEN") ?: "",
                serverToken = System.getenv("KERUTA_SERVER_TOKEN") ?: "",
                queueId = System.getenv("KERUTA_QUEUE_ID")?.toLongOrNull() ?: 1L
            )
        }
    }
}
