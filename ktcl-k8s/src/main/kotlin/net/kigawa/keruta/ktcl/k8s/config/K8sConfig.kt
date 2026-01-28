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
) {
    companion object {
        fun fromEnvironment(): K8sConfig = K8sConfig(
            ktseHost = System.getenv("KTSE_HOST") ?: "localhost",
            ktsePort = System.getenv("KTSE_PORT")?.toInt() ?: 8080,
            ktseUseTls = System.getenv("KTSE_USE_TLS")?.toBoolean() ?: false,
            userToken = requireEnv("KERUTA_USER_TOKEN"),
            serverToken = requireEnv("KERUTA_SERVER_TOKEN"),
            queueId = requireEnv("KERUTA_QUEUE_ID").toLong(),
            k8sNamespace = System.getenv("K8S_NAMESPACE") ?: "default",
            k8sJobTemplate = System.getenv("K8S_JOB_TEMPLATE") ?: "resources/job-template.yaml",
            k8sUseInCluster = System.getenv("K8S_USE_IN_CLUSTER")?.toBoolean() ?: true,
            k8sKubeConfigPath = System.getenv("K8S_KUBECONFIG_PATH"),
            k8sJobTimeout = System.getenv("K8S_JOB_TIMEOUT")?.toLong() ?: 600,
        )

        private fun requireEnv(name: String): String =
            System.getenv(name) ?: error("Required environment variable '$name' is not set")
    }
}
