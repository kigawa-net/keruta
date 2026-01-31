package net.kigawa.keruta.ktcl.k8s.config
/**
 * IDプロバイダー設定
 */
data class IdpConfig(
    val issuer: String,
    val audience: String
) {
    companion object {
        fun load(config: io.ktor.server.config.ApplicationConfig): IdpConfig {
            return IdpConfig(
                issuer = config.property("idp.issuer").getString(),
                audience = config.propertyOrNull("idp.audience")?.getString() ?: "keruta"
            )
        }
    }
}
