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

    // task-executorコンテナ向けKTSE接続設定（K8s内部DNSで解決可能なホスト名）
    val taskExecutorKtseHost: String,
    val taskExecutorKtsePort: Int,
    val taskExecutorKtseUseTls: Boolean,

    // Kubernetes設定
    val k8sNamespace: String,
    val k8sUseInCluster: Boolean,
    val k8sKubeConfigPath: String?,
    val k8sJobTimeout: Long,
    val pvcStorageClassName: String?,
    val pvcStorageSize: String,

    // Webモード設定
    val webMode: Boolean,
    val webPort: Int,
) {
    companion object {

        /**
         * 環境変数から直接読み込む（既存コードとの互換性のため）
         */
        fun fromEnvironment(): K8sConfigCompat {
            return K8sConfigCompat(
                ktseHost = System.getenv("KTSE_HOST") ?: "localhost",
                ktsePort = System.getenv("KTSE_PORT")?.toInt() ?: 8080,
                ktseUseTls = System.getenv("KTSE_USE_TLS")?.toBoolean() ?: false,
                taskExecutorKtseHost = System.getenv("TASK_EXECUTOR_KTSE_HOST")
                    ?: System.getenv("KTSE_HOST") ?: "localhost",
                taskExecutorKtsePort = System.getenv("TASK_EXECUTOR_KTSE_PORT")?.toInt()
                    ?: System.getenv("KTSE_PORT")?.toInt() ?: 8080,
                taskExecutorKtseUseTls = System.getenv("TASK_EXECUTOR_KTSE_USE_TLS")?.toBoolean()
                    ?: System.getenv("KTSE_USE_TLS")?.toBoolean() ?: false,
                k8sNamespace = getEnvOrProperty("K8S_NAMESPACE") ?: "default",
                k8sUseInCluster = getEnvOrProperty("K8S_USE_IN_CLUSTER")?.toBoolean() ?: true,
                k8sKubeConfigPath = getEnvOrProperty("K8S_KUBECONFIG_PATH"),
                k8sJobTimeout = getEnvOrProperty("K8S_JOB_TIMEOUT")?.toLongOrNull() ?: 600,
                pvcStorageClassName = getEnvOrProperty("K8S_PVC_STORAGE_CLASS"),
                pvcStorageSize = getEnvOrProperty("K8S_PVC_STORAGE_SIZE") ?: "1Gi",
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
