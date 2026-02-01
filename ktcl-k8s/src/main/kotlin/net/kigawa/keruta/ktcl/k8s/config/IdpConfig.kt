package net.kigawa.keruta.ktcl.k8s.config

import java.net.URI

/**
 * IDプロバイダー設定
 */
data class IdpConfig(
    val strIssuer: String,
    val clientId: String,
    val issuer: URI,
) {
    companion object {
        fun load(config: io.ktor.server.config.ApplicationConfig): IdpConfig {
            return IdpConfig(
                issuer = URI(config.property("idp.issuer").getString()),
                strIssuer = config.property("idp.issuer").getString(),
                clientId = config.propertyOrNull("idp.audience")?.getString() ?: "keruta"
            )
        }
    }
}
