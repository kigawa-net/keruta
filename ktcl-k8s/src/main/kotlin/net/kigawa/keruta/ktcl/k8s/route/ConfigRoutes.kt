package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.auth.AuthGuard
import net.kigawa.keruta.ktcl.k8s.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.auth.KeycloakConfig
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.dto.*
import net.kigawa.keruta.ktcl.k8s.login.ProviderListClient
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserClaudeConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.base.auth.key.JavaKeyPairInitializer
import net.kigawa.keruta.ktcp.domain.auth.key.PemKey
import net.kigawa.keruta.ktcp.domain.client.wellknown.KerutaWellKnownJson
import net.kigawa.keruta.ktcp.domain.client.wellknown.ObjectPropertyJson
import net.kigawa.keruta.ktcp.domain.client.wellknown.StringPropertyJson
import net.kigawa.keruta.ktcp.infra.client.NimbusdsJwksGenerator
import net.kigawa.keruta.ktcp.usecase.client.JwksJsonGenerator
import net.kigawa.kodel.api.log.LoggerFactory

class ConfigRoutes(
    keycloakConfig: KeycloakConfig, val appConfig: AppConfig,
    private val privateKey: PemKey,
    private val userClaudeConfigDao: UserClaudeConfigDao,
    private val userTokenDao: UserTokenDao,
    javaKeyPairInitializer: JavaKeyPairInitializer,
    authenticationHelper: AuthenticationHelper,
    auth0JwtVerifier: Auth0JwtVerifier,
    private val providerListClient: ProviderListClient,
) {
    private val logger = LoggerFactory.get("ConfigRoutes")
    private val authGuard = AuthGuard(authenticationHelper)
    private val authRoute = AuthRoutes(keycloakConfig, authenticationHelper, auth0JwtVerifier)

    private val jwksJsonGenerator: JwksJsonGenerator = NimbusdsJwksGenerator(javaKeyPairInitializer)
    fun configureConfigRoutes(
        route: Route,
    ) = route.apply {
        // 認証ルート
        authRoute.configure(this)

        route("/.well-known") {
            // .well-known エンドポイント（認証不要）
            get("keruta.json") {
                val appConfig = appConfig
                val issuer = appConfig.keruta.ownIssuer
                val loginEndpoint = issuer.plusPath("/login")

                val response = KerutaWellKnownJson(
                    version = "1.0.0",
                    issuer = issuer.toStrUrl(),
                    login = loginEndpoint.toString(),
                    queueProperties = ObjectPropertyJson(
                        listOf(
                            ObjectPropertyJson.Field(
                                "git-repo", "git repo url",
                                StringPropertyJson(true)
                            )
                        )
                    )
                )
                call.respond(response)
            }
            get("jwks.json") {
                val jwksJson = jwksJsonGenerator.generate(privateKey)
                call.respond(
                    jwksJson
                )
            }
        }
        // フォームでのGitHub Token保存
        post("/config/github") {
            authGuard.requireAuth(call) { user ->
                val params = call.receiveParameters()
                val githubToken = params["githubToken"]?.trim() ?: ""
                if (githubToken.isEmpty()) {
                    call.respondRedirect("/?error=token_required")
                    return@requireAuth
                }
                userTokenDao.saveOrUpdateGithubToken(user.userId, githubToken)
                logger.info("GitHub token updated for user: ${user.userId}")
                call.respondRedirect("/?success=github_token_saved")
            }
        }

        // フォームでのClaude Code APIキー保存
        post("/config/claudecode") {
            authGuard.requireAuth(call) { user ->
                val params = call.receiveParameters()
                val anthropicApiKey = params["anthropicApiKey"]?.trim() ?: ""
                if (anthropicApiKey.isEmpty()) {
                    call.respondRedirect("/?error=api_key_required")
                    return@requireAuth
                }
                userClaudeConfigDao.saveOrUpdate(user.userId, anthropicApiKey)
                logger.info("Claude Code API key updated for user: ${user.userId}")
                call.respondRedirect("/?success=claude_key_saved")
            }
        }

        // フォームでのログアウト
        post("/config/logout") {
            call.sessions.clear<net.kigawa.keruta.ktcl.k8s.auth.UserSession>()
            call.respondRedirect("/login")
        }

        route("/api/config") {
            get {
                authGuard.requireAuth(call) { user ->
                    val appConfig = appConfig
                    val hasApiKey = userClaudeConfigDao.get(user.userId) != null
                    val hasGithubToken = userTokenDao.getGithubToken(user.userId) != null
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
                        hasGithubToken = hasGithubToken,
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

            put("/github") {
                authGuard.requireAuth(call) { user ->
                    val request = call.receive<UpdateGithubTokenRequest>()
                    val githubToken = request.githubToken.trim()
                    if (githubToken.isEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "githubToken is required"))
                        return@requireAuth
                    }

                    userTokenDao.saveOrUpdateGithubToken(user.userId, githubToken)
                    logger.info("GitHub token updated for user: ${user.userId}")

                    call.respond(mapOf("success" to true, "message" to "GitHub token updated"))
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
        route("/api/providers") {
            get {
                authGuard.requireAuth(call) { user ->
                    val providers = providerListClient.listProviders(user.token)
                    call.respond(ProvidersResponse(providers))
                }
            }
        }
    }

}
