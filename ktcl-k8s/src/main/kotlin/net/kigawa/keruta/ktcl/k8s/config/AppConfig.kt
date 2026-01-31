package net.kigawa.keruta.ktcl.k8s.config

import io.ktor.server.application.*
import io.ktor.util.*

/**
 * アプリケーション全体の設定を保持するクラス
 */
data class AppConfig(
    val server: ServerConfig,
    val idp: IdpConfig,
    val keruta: KerutaConfig,
    val ktse: KtseConfig,
    val k8s: K8sSettings
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
                k8s = K8sSettings.fromEnvironment()
            )
        }
    }
}

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

/**
 * Kubernetes設定
 */
data class K8sSettings(
    val namespace: String,
    val jobTemplate: String,
    val useInCluster: Boolean,
    val kubeConfigPath: String?,
    val jobTimeout: Long
) {
    companion object {
        fun fromEnvironment(): K8sSettings {
            return K8sSettings(
                namespace = getEnvOrProperty("K8S_NAMESPACE") ?: "default",
                jobTemplate = System.getenv("K8S_JOB_TEMPLATE") ?: "resources/job-template.yaml",
                useInCluster = getEnvOrProperty("K8S_USE_IN_CLUSTER")?.toBoolean() ?: true,
                kubeConfigPath = getEnvOrProperty("K8S_KUBECONFIG_PATH"),
                jobTimeout = getEnvOrProperty("K8S_JOB_TIMEOUT")?.toLongOrNull() ?: 600
            )
        }

        private fun getEnvOrProperty(name: String): String? =
            System.getenv(name) ?: System.getProperty(name)
    }
}

/**
 * Application属性キー
 */
val APP_CONFIG_KEY = AttributeKey<AppConfig>("AppConfig")

/**
 * Applicationから設定を取得する拡張プロパティ
 */
val Application.appConfig: AppConfig
    get() = attributes[APP_CONFIG_KEY]