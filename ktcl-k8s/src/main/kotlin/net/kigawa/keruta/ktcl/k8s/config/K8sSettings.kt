package net.kigawa.keruta.ktcl.k8s.config

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
