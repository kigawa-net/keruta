package net.kigawa.keruta.ktcl.k8s.route

import com.auth0.jwk.JwkProvider
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.k8s.auth.AuthGuard
import net.kigawa.keruta.ktcl.k8s.auth.KeycloakConfig
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.dto.*
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserClaudeConfigDao
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.model.auth.key.PrivateKey
import net.kigawa.kodel.api.log.LoggerFactory

class ConfigRoutes(
    jwkProvider: JwkProvider,
    keycloakConfig: KeycloakConfig, val appConfig: AppConfig,
    auth0JwtVerifier: Auth0JwtVerifier,
    privateKey: PrivateKey,
    private val userClaudeConfigDao: UserClaudeConfigDao,
) {
    private val logger = LoggerFactory.get("ConfigRoutes")
    private val authGuard = AuthGuard(auth0JwtVerifier, privateKey)
    private val authRoute = AuthRoutes(jwkProvider, keycloakConfig, auth0JwtVerifier, privateKey)

    fun configureConfigRoutes(
        route: Route,
    ) = route.apply {
        // 認証ルート
        authRoute.configure(this)

        // .well-known エンドポイント（認証不要）
        get("/.well-known/keruta.json") {
            val appConfig = appConfig
            val issuer = appConfig.keruta.ownIssuer
            val loginEndpoint = issuer.plusPath("/login")

            val response = WellKnownKerutaResponse(
                service = "keruta-ktcl-k8s",
                version = "1.0.0",
                issuer = issuer.toStrUrl(),
                login = loginEndpoint.toString()
            )
            call.respond(response)
        }
        route("/api/config") {
            get {
                authGuard.requireAuth(call) { user ->
                    val appConfig = appConfig
                    val hasApiKey = userClaudeConfigDao.get(user.userId) != null
                    val response = ConfigResponse(
                        kubernetes = KubernetesConfig(
                            namespace = appConfig.k8s.namespace,
                            useInCluster = appConfig.k8s.useInCluster,
                            kubeconfigPath = appConfig.k8s.kubeConfigPath,
                            jobTimeout = appConfig.k8s.jobTimeout
                        ),
                        queue = QueueConfig(
                            queueId = appConfig.ktse.queueId
                        ),
                        claudeCode = ClaudeCodeConfig(
                            hasApiKey = hasApiKey
                        ),
                    )
                    call.respond(response)
                }
            }

            put("/kubernetes") {
                authGuard.requireAuth(call) { _ ->
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
                authGuard.requireAuth(call) { _ ->
                    val request = call.receive<UpdateQueueConfigRequest>()
                    logger.info("Updating Queue config: $request")

                    // 環境変数を更新（ランタイム設定）
                    System.setProperty("KERUTA_QUEUE_ID", request.queueId.toString())

                    logger.info("Queue config updated successfully")
                    call.respond(mapOf("success" to true, "message" to "Queue configuration updated"))
                }
            }

            put("/claudecode") {
                authGuard.requireAuth(call) { user ->
                    val request = call.receive<UpdateClaudeCodeConfigRequest>()
                    val anthropicApiKey = request.anthropicApiKey.trim()
                    if (anthropicApiKey.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "anthropicApiKey is required"))
                        return@requireAuth
                    }

                    userClaudeConfigDao.saveOrUpdate(user.userId, anthropicApiKey)
                    logger.info("Claude Code API key updated for user: ${user.userId}")

                    call.respond(mapOf("success" to true, "message" to "Claude Code API key updated"))
                }
            }
        }
    }

}
