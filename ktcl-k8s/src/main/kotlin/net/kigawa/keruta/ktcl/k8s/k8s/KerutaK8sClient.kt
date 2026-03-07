package net.kigawa.keruta.ktcl.k8s.k8s

import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.k8s.auth.TokenRefresher
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.connection.ConnectionManager
import net.kigawa.keruta.ktcl.k8s.entrypoint.ClientEntrypointsFactory
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcl.k8s.task.TaskExecutorFactory
import net.kigawa.keruta.ktcl.k8s.task.TaskReceiver
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.client.KtcpSession
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.kodel.api.log.LoggerFactory
import kotlin.time.Duration.Companion.seconds

class KerutaK8sClient(
    private val config: K8sConfig,
    private val userTokenDao: UserTokenDao,
    private val tokenRefresher: TokenRefresher,
) {
    private val logger = LoggerFactory.get("KerutaK8sClient")
    private val serializer = JsonKerutaSerializer()
    private val ktcpClient = KtcpClient()
    private val serverToken = System.getenv("KERUTA_SERVER_TOKEN")
        ?: throw IllegalStateException("KERUTA_SERVER_TOKEN is required")
    private val concurrentCount = 1

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta K8s Client" }
        (0 until concurrentCount).map {
            launch {
                while (isActive) {
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
                            runTaskReceiver(userId, tokenResponse.accessToken)
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            logger.severe { "Failed to refresh token ${e.message}" }
                            e.printStackTrace()
                        }
                    }

                    delay(30.seconds)
                }
            }
        }.joinAll()
    }

    private suspend fun runTaskReceiver(userId: String, accessToken: String) {
        val connectionManager = ConnectionManager(config)
        val connection = try {
            connectionManager.connect()
        } catch (e: Exception) {
            logger.severe { "Failed to connect to KTSE for user $userId: ${e.message}" }
            return
        }

        val session = KtcpSession(connection)
        val ctx = ClientCtx(serializer, session)

        // 認証
        val authMsg = ServerAuthRequestMsg(
            userToken = accessToken,
            serverToken = serverToken
        )
        ktcpClient.ktcpServerEntrypoints.authRequestEntrypoint.access(authMsg, ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send auth request for user $userId" }
                return
            }

        logger.info { "Authentication request sent for user $userId" }

        // タスクエグゼキューターとクライアントエントリポイントを作成
        val taskExecutor = TaskExecutorFactory(config, ktcpClient).create()
        val clientEntrypoints = ClientEntrypointsFactory(ktcpClient, config, taskExecutor).create()

        // タスク受信ループを開始
        val taskReceiver = TaskReceiver(connection, clientEntrypoints)
        taskReceiver.startReceiving(ctx)
    }

}
