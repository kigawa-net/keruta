package net.kigawa.keruta.ktcl.k8s.k8s

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import java.io.FileReader

object K8sClientFactory {
    fun createClient(config: K8sConfig): ApiClient {
        val client = if (config.k8sUseInCluster) {
            // In-cluster認証（Pod内で実行時）
            ClientBuilder.cluster().build()
        } else {
            // kubeconfig認証（ローカル開発時）
            val kubeConfigPath = config.k8sKubeConfigPath
                ?: "${System.getProperty("user.home")}/.kube/config"
            ClientBuilder.kubeconfig(
                KubeConfig.loadKubeConfig(FileReader(kubeConfigPath))
            ).build()
        }

        // デフォルトクライアントとして設定
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client)
        return client
    }
}
