package net.kigawa.keruta.ktcl.k8s.web.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.requireAuth
import net.kigawa.kodel.api.log.LoggerFactory

private val logger = LoggerFactory.get("ConfigRoutes")

@Serializable
data class ConfigResponse(
    val kubernetes: KubernetesConfig,
    val queue: QueueConfig
)

@Serializable
data class KubernetesConfig(
    val namespace: String,
    val useInCluster: Boolean,
    val kubeconfigPath: String?,
    val jobTimeout: Long
)

@Serializable
data class QueueConfig(
    val queueId: Long
)

@Serializable
data class UpdateKubernetesConfigRequest(
    val namespace: String? = null,
    val useInCluster: Boolean? = null,
    val kubeconfigPath: String? = null,
    val jobTimeout: Long? = null
)

@Serializable
data class UpdateQueueConfigRequest(
    val queueId: Long
)

fun Route.configureConfigRoutes() {
    // 認証ルート
    configureAuthRoutes()

    route("/api/config") {
        get {
            call.requireAuth { _ ->
                val config = K8sConfig.fromEnvironment()
                val response = ConfigResponse(
                    kubernetes = KubernetesConfig(
                        namespace = config.k8sNamespace,
                        useInCluster = config.k8sUseInCluster,
                        kubeconfigPath = config.k8sKubeConfigPath,
                        jobTimeout = config.k8sJobTimeout
                    ),
                    queue = QueueConfig(
                        queueId = config.queueId
                    )
                )
                call.respond(response)
            }
        }

        put("/kubernetes") {
            call.requireAuth { _ ->
                val request = call.receive<UpdateKubernetesConfigRequest>()
                logger.info("Updating Kubernetes config: $request")

                // 環境変数を更新（ランタイム設定）
                request.namespace?.let { System.setProperty("K8S_NAMESPACE", it) }
                request.useInCluster?.let { System.setProperty("K8S_USE_IN_CLUSTER", it.toString()) }
                request.kubeconfigPath?.let { System.setProperty("K8S_KUBECONFIG_PATH", it) }
                request.jobTimeout?.let { System.setProperty("K8S_JOB_TIMEOUT", it.toString()) }

                logger.info("Kubernetes config updated successfully")
                call.respond(mapOf("success" to true, "message" to "Configuration updated"))
            }
        }

        put("/queue") {
            call.requireAuth { _ ->
                val request = call.receive<UpdateQueueConfigRequest>()
                logger.info("Updating Queue config: $request")

                // 環境変数を更新（ランタイム設定）
                System.setProperty("KERUTA_QUEUE_ID", request.queueId.toString())

                logger.info("Queue config updated successfully")
                call.respond(mapOf("success" to true, "message" to "Queue configuration updated"))
            }
        }
    }
}
