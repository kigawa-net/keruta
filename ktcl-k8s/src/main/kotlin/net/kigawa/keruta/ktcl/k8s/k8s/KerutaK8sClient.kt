package net.kigawa.keruta.ktcl.k8s.k8s

import com.auth0.jwt.JWT
import kotlinx.coroutines.*
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
import net.kigawa.kodel.api.log.LoggerFactory
import kotlin.time.Duration.Companion.seconds

class KerutaK8sClient(
    private val config: K8sConfig,
    private val userTokenDao: UserTokenDao,
    private val tokenRefresher: TokenRefresher,
    private val providerTokenCreator: ProviderTokenCreator,
) {
    private val logger = LoggerFactory.get("KerutaK8sClient")
    private val serializer = JsonKerutaSerializer()
    private val ktcpClient = KtcpClient()
    private val concurrentCount = 1

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta K8s Client" }
        (0 until concurrentCount).map {
            launch {
                while (isActive) {
                    var received = false
                    userTokenDao.getRefreshTokens().forEach {
                        try {
                            val (userId, refreshToken) = it

                            val tokenResponse = try {
                                tokenRefresher.refresh(refreshToken)
                            } catch (e: Exception) {
                                logger.severe { "Token refresh failed for user $userId: ${e.message}" }
                                delay(30.seconds)
                                return@forEach
                            }

                            tokenResponse.refreshToken?.let { userTokenDao.saveOrUpdate(userId, it) }
                            logger.info { "Access token refreshed for user $userId" }

                            // accessTokenを使ってKTSEに接続しタスク受信ループを実行する
                            received = runTaskReceiver(userId, tokenResponse.accessToken) || received
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

    private suspend fun runTaskReceiver(userId: String, accessToken: String): Boolean {
        val connectionManager = ConnectionManager(config)
        val connection = try {
            connectionManager.connect()
        } catch (e: Exception) {
            logger.severe { "Failed to connect to KTSE for user $userId: ${e.message}" }
            return false
        }

        val session = KtcpSession(connection)
        val ctx = ClientCtx(serializer, session)
        val providerToken = providerTokenCreator.create(JWT.decode(accessToken).subject)
        // 認証
        val authMsg = ServerAuthRequestMsg(
            userToken = accessToken,
            serverToken = providerToken.createdToken.rawToken
        )
        ktcpClient.ktcpServerEntrypoints.authRequestEntrypoint.access(authMsg, ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send auth request for user $userId" }
                return false
            }

        logger.info { "Authentication request sent for user $userId" }


        // タスク受信ループを開始
        val taskReceiver = TaskReceiver(connection, config, ktcpClient)
        return taskReceiver.startReceiving(ctx, userId)
    }

}
