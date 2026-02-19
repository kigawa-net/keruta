package net.kigawa.keruta.ktcl.mobile.config

data class MobileConfig(
    val websocketUrl: String,
    val keycloakUrl: String,
    val keycloakRealm: String,
    val keycloakClientId: String,
    val apiBaseUrl: String,
    val ktseAudience: String,
) {
    companion object {
        fun default(): MobileConfig {
            return MobileConfig(
                websocketUrl = "wss://ktse-dev.kigawa.net/ws/ktcp",
                keycloakUrl = "https://user.kigawa.net/",
                keycloakRealm = "develop",
                keycloakClientId = "keruta",
                apiBaseUrl = "https://keruta-dev.kigawa.net/",
                ktseAudience = "keruta",
            )
        }
    }
}
