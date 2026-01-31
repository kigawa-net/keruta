package net.kigawa.keruta.ktcl.k8s.config

/**
 * 旧K8sConfigとの互換性のためのデータクラス
 * 既存のコードを変更せずに使えるように、古いフィールド名を保持
 */
data class K8sConfigCompat(
    // KTSE接続設定
    val ktseHost: String,
    val ktsePort: Int,
    val ktseUseTls: Boolean,
    val userToken: String,
    val serverToken: String,
    val queueId: Long,

    // Kubernetes設定
    val k8sNamespace: String,
    val k8sJobTemplate: String,
    val k8sUseInCluster: Boolean,
    val k8sKubeConfigPath: String?,
    val k8sJobTimeout: Long,

    // Webモード設定
    val webMode: Boolean,
    val webPort: Int,
) {
    companion object {
        /**
         * AppConfigから互換性レイヤーを生成
         */
        fun from(appConfig: AppConfig): K8sConfigCompat {
            return K8sConfigCompat(
                ktseHost = appConfig.ktse.host,
                ktsePort = appConfig.ktse.port,
                ktseUseTls = appConfig.ktse.useTls,
                userToken = appConfig.ktse.userToken,
                serverToken = appConfig.ktse.serverToken,
                queueId = appConfig.ktse.queueId,
                k8sNamespace = appConfig.k8s.namespace,
                k8sJobTemplate = appConfig.k8s.jobTemplate,
                k8sUseInCluster = appConfig.k8s.useInCluster,
                k8sKubeConfigPath = appConfig.k8s.kubeConfigPath,
                k8sJobTimeout = appConfig.k8s.jobTimeout,
                webMode = appConfig.server.webMode,
                webPort = appConfig.server.port
            )
        }

        /**
         * 環境変数から直接読み込む（既存コードとの互換性のため）
         */
        fun fromEnvironment(): K8sConfigCompat {
            return K8sConfigCompat(
                ktseHost = System.getenv("KTSE_HOST") ?: "localhost",
                ktsePort = System.getenv("KTSE_PORT")?.toInt() ?: 8080,
                ktseUseTls = System.getenv("KTSE_USE_TLS")?.toBoolean() ?: false,
                userToken = System.getenv("KERUTA_USER_TOKEN") ?: "",
                serverToken = System.getenv("KERUTA_SERVER_TOKEN") ?: "",
                queueId = System.getenv("KERUTA_QUEUE_ID")?.toLongOrNull() ?: 1L,
                k8sNamespace = getEnvOrProperty("K8S_NAMESPACE") ?: "default",
                k8sJobTemplate = System.getenv("K8S_JOB_TEMPLATE") ?: "resources/job-template.yaml",
                k8sUseInCluster = getEnvOrProperty("K8S_USE_IN_CLUSTER")?.toBoolean() ?: true,
                k8sKubeConfigPath = getEnvOrProperty("K8S_KUBECONFIG_PATH"),
                k8sJobTimeout = getEnvOrProperty("K8S_JOB_TIMEOUT")?.toLongOrNull() ?: 600,
                webMode = System.getenv("KTCL_K8S_WEB_MODE")?.toBoolean() ?: false,
                webPort = System.getenv("KTCL_K8S_WEB_PORT")?.toInt() ?: 8081,
            )
        }

        private fun getEnvOrProperty(name: String): String? =
            System.getenv(name) ?: System.getProperty(name)
    }
}

/**
 * 後方互換性のために、旧K8sConfig名でエイリアスを作成
 */
typealias K8sConfig = K8sConfigCompat