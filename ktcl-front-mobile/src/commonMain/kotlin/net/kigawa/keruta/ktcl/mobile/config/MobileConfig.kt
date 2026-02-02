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
                websocketUrl = "ws://localhost:8080/ws/ktcp",
                keycloakUrl = "https://user.kigawa.net/",
                keycloakRealm = "develop",
                keycloakClientId = "keruta",
                apiBaseUrl = "http://localhost:5173/",
                ktseAudience = "keruta",
            )
        }
    }
}
