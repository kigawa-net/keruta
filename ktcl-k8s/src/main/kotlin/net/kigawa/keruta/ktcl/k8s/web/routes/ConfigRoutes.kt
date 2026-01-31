package net.kigawa.keruta.ktcl.k8s.web.routes

import com.auth0.jwk.JwkProvider
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.web.auth.AuthGuard
import net.kigawa.keruta.ktcl.k8s.web.auth.KeycloakConfig
import net.kigawa.keruta.ktcl.k8s.web.dto.*
import net.kigawa.kodel.api.log.LoggerFactory

class ConfigRoutes(val jwkProvider: JwkProvider, val keycloakConfig: KeycloakConfig, val appConfig: AppConfig) {
    private val logger = LoggerFactory.get("ConfigRoutes")

    fun configureConfigRoutes(
        route: Route,
    ) = route.apply {
        // 認証ルート
        AuthRoutes(jwkProvider, keycloakConfig).configure(this)

        // .well-known エンドポイント（認証不要）
        get("/.well-known/keruta.json") {
            val appConfig = appConfig
            val issuer = appConfig.keruta.ownIssuer
            val loginUrl = "$issuer/protocol/openid-connect/auth"

            val response = WellKnownKerutaResponse(
                service = "keruta-ktcl-k8s",
                version = "1.0.0",
                auth = AuthInfo(
                    issuer = issuer,
                    loginUrl = loginUrl
                )
            )
            call.respond(response)
        }

        route("/api/config") {
            get {

                AuthGuard(jwkProvider, keycloakConfig).requireAuth(call) { _ ->
                    val appConfig = appConfig
                    val response = ConfigResponse(
                        kubernetes = KubernetesConfig(
                            namespace = appConfig.k8s.namespace,
                            useInCluster = appConfig.k8s.useInCluster,
                            kubeconfigPath = appConfig.k8s.kubeConfigPath,
                            jobTimeout = appConfig.k8s.jobTimeout
                        ),
                        queue = QueueConfig(
                            queueId = appConfig.ktse.queueId
                        )
                    )
                    call.respond(response)
                }
            }

            put("/kubernetes") {
                AuthGuard(jwkProvider, keycloakConfig).requireAuth(call) { _ ->
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
                AuthGuard(jwkProvider, keycloakConfig).requireAuth(call) { _ ->
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

}

