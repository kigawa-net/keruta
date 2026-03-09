package net.kigawa.keruta.ktcl.k8s.config

import net.kigawa.kodel.api.net.Url

/**
 * Keruta自身の設定
 */
data class KerutaConfig(
    val ownIssuer: Url
) {
    companion object {
        fun load(config: io.ktor.server.config.ApplicationConfig): KerutaConfig {
            return KerutaConfig(
                ownIssuer = Url.parse(config.property("keruta.ownIssuer").getString())
            )
        }
    }
}
