package net.kigawa.keruta.ktcl.k8s.k8s

/**
 * Kubernetes設定
 */
data class K8sSettings(
    val namespace: String,
    val useInCluster: Boolean,
    val kubeConfigPath: String?,
    val jobTimeout: Long,
) {
    companion object {
        fun fromEnvironment(): K8sSettings {
            return K8sSettings(
                namespace = getEnvOrProperty("K8S_NAMESPACE") ?: throw IllegalStateException(
                    "K8S_NAMESPACE is required"
                ),
                useInCluster = getEnvOrProperty("K8S_USE_IN_CLUSTER")?.toBoolean() ?: true,
                kubeConfigPath = getEnvOrProperty("K8S_KUBECONFIG_PATH"),
                jobTimeout = getEnvOrProperty("K8S_JOB_TIMEOUT")?.toLongOrNull() ?: 600
            )
        }

        private fun getEnvOrProperty(name: String): String? =
            System.getenv(name) ?: System.getProperty(name)
    }
}
