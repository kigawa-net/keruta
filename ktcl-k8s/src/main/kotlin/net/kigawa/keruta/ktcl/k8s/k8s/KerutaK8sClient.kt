package net.kigawa.keruta.ktcl.k8s.k8s

import com.auth0.jwt.JWT
import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.k8s.auth.TokenRefreshException
import net.kigawa.keruta.ktcl.k8s.auth.TokenRefresher
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.connection.ConnectionManager
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcl.k8s.task.TaskReceiver
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.client.KtcpSession
import net.kigawa.keruta.ktcp.domain.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.usecase.client.ProviderTokenCreator
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import kotlin.time.Duration.Companion.seconds

class KerutaK8sClient(
    private val config: K8sConfig,
    private val userTokenDao: UserTokenDao,
    private val tokenRefresher: TokenRefresher,
    private val providerTokenCreator: ProviderTokenCreator,
    private val ktclIssuer: String,
) {
    private val logger = getKogger()
    private val serializer = JsonKerutaSerializer()
    private val ktcpClient = KtcpClient()
    private val concurrentCount = 1

    suspend fun start() = coroutineScope {
        logger.debug { "Starting Keruta K8s Client" }
        (0 until concurrentCount).map { workerNum ->
            logger.debug { "Starting worker thread ${workerNum + 1} of $concurrentCount" }
            launch {
                logger.debug { "Worker thread ${workerNum + 1} of $concurrentCount started" }
                while (isActive) {
                    logger.debug { "Worker thread ${workerNum + 1} of $concurrentCount is active" }
                    var received = false
                    userTokenDao.getRefreshTokens().forEach {
                        logger.debug { "Refreshing token for user ${it.userSubject}" }
                        try {
                            val tokenResponse = try {
                                tokenRefresher.refresh(it.refreshToken)
                            } catch (e: TokenRefreshException) {
                                logger.severe { "Token refresh failed for user ${it.userSubject} (token expired or invalid): ${e.message}" }
                                userTokenDao.deleteRefreshToken(it.userSubject, it.userIssuer)
                                return@forEach
                            } catch (e: Exception) {
                                logger.severe { "Token refresh failed for user ${it.userSubject}: ${e.message}" }
                                delay(30.seconds)
                                return@forEach
                            }

                            tokenResponse.refreshToken?.let { token ->
                                userTokenDao.saveOrUpdate(it.userSubject, it.userIssuer, it.userAudience, token)
                            }
                            logger.info { "Access token refreshed for user ${it.userSubject}" }

                            // accessTokenを使ってKTSEに接続しタスク受信ループを実行する
                            received = runTaskReceiver(
                                it.userSubject, it.userIssuer, tokenResponse.accessToken
                            ) || received
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            logger.severe { "Failed to refresh token ${e.message}" }
                            e.printStackTrace()
                        }
                    }
                    if (received) delay(1.seconds)
                    else delay(30.seconds)
                }
            }
        }.joinAll()
    }

    private suspend fun runTaskReceiver(userSubject: String, userIssuer: String, accessToken: String): Boolean {
        logger.debug { "Running task receiver for user $userSubject" }
        val connectionManager = ConnectionManager(config)
        logger.debug { "Connecting to KTSE for user $userSubject" }
        val connection = try {
            connectionManager.connect()
        } catch (e: Exception) {
            logger.severe { "Failed to connect to KTSE for user $userSubject: ${e.message}" }
            return false
        }
        logger.debug { "Connected to KTSE for user $userSubject" }
        val session = KtcpSession(connection)
        logger.debug { "Created KTSE session for user $userSubject" }
        val ctx = ClientCtx(serializer, session)
        logger.debug { "Creating provider token for user $userSubject" }
        val providerToken = providerTokenCreator.create(JWT.decode(accessToken).subject)
        logger.debug { "Provider token created for user $userSubject" }
        val authMsg = ServerAuthRequestMsg(
            userToken = accessToken,
            serverToken = providerToken.createdToken.rawToken
        )
        logger.debug { "Sending authentication request for user $userSubject" }
        ktcpClient.ktcpServerEntrypoints.authRequestEntrypoint.access(authMsg, ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send auth request for user $userSubject" }
                return false
            }
        logger.debug { "Authentication request sent for user $userSubject" }
        val apiClient = K8sClientFactory.createClient(config)
        logger.debug { "Kubernetes API client created for user $userSubject" }
        val templateLoader = JobTemplateLoader("job-template.yaml")
        logger.debug { "Job template loader created for user $userSubject" }
        val jobExecutor = K8sJobExecutor(apiClient, config, templateLoader)
        logger.debug { "Job executor created for user $userSubject" }
        val taskReceiver = TaskReceiver(connection, ktcpClient, jobExecutor, ktclIssuer, userTokenDao)
        logger.debug { "Starting task receiver for user $userSubject" }
        return taskReceiver.startReceiving(ctx, userSubject, userIssuer, accessToken, providerToken.createdToken.rawToken)
    }

}
