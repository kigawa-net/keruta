package net.kigawa.keruta.ktcl.k8s.config

/**
 * アプリケーション全体の設定を保持するクラス
 */
data class AppConfig(
    val server: ServerConfig,
    val idp: IdpConfig,
    val keruta: KerutaConfig,
    val ktse: KtseConfig,
    val k8s: K8sSettings,
    val cors: CorsConfig,
    val auth: AuthConfig,
) {
    companion object {
        /**
         * ApplicationConfig と環境変数から設定を読み込む
         */
        fun load(applicationConfig: io.ktor.server.config.ApplicationConfig): AppConfig {
            return AppConfig(
                server = ServerConfig.load(applicationConfig),
                idp = IdpConfig.load(applicationConfig),
                keruta = KerutaConfig.load(applicationConfig),
                ktse = KtseConfig.fromEnvironment(),
                k8s = K8sSettings.fromEnvironment(),
                cors = CorsConfig.fromEnvironment(),
                auth = AuthConfig.load()
            )
        }
    }
}



