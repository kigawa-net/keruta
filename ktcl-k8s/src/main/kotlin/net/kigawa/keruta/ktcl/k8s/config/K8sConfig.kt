package net.kigawa.keruta.ktcl.k8s.config

data class K8sConfig(
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
        fun fromEnvironment(): K8sConfig = K8sConfig(
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

        private fun requireEnv(name: String): String =
            System.getenv(name) ?: error("Required environment variable '$name' is not set")

        private fun getEnvOrProperty(name: String): String? =
            System.getenv(name) ?: System.getProperty(name)
    }
}
