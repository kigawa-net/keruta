package net.kigawa.keruta.ktcl.k8s.config

/**
 * Keruta自身の設定
 */
data class KerutaConfig(
    val ownIssuer: String
) {
    companion object {
        fun load(config: io.ktor.server.config.ApplicationConfig): KerutaConfig {
            return KerutaConfig(
                ownIssuer = config.property("keruta.ownIssuer").getString()
            )
        }
    }
}
