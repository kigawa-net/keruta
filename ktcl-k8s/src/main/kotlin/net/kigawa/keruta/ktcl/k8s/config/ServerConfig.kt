package net.kigawa.keruta.ktcl.k8s.config

/**
 * サーバー設定
 */
data class ServerConfig(
    val port: Int,
    val webMode: Boolean
) {
    companion object {
        fun load(config: io.ktor.server.config.ApplicationConfig): ServerConfig {
            return ServerConfig(
                port = config.property("ktor.deployment.port").getString().toInt(),
                webMode = System.getenv("KTCL_K8S_WEB_MODE")?.toBoolean() ?: false
            )
        }
    }
}
