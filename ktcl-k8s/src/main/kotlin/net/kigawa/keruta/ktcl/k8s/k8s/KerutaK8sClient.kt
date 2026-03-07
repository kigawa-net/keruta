package net.kigawa.keruta.ktcl.k8s.k8s

import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.k8s.auth.TokenRefresher
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.kodel.api.log.LoggerFactory
import kotlin.time.Duration.Companion.seconds

class KerutaK8sClient(
    private val config: K8sConfig,
    private val userTokenDao: UserTokenDao,
    private val tokenRefresher: TokenRefresher,
) {
    private val logger = LoggerFactory.get("KerutaK8sClient")
    private val concurrentCount = 1

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta K8s Client" }
        (0 until concurrentCount).map {
            launch {
                while (isActive) {
                    userTokenDao.getRefreshTokens().forEach {
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

                        // TODO: accessTokenを使ってKTSEに接続しタスク受信ループを実行する
                    }

                    delay(30.seconds)
                }
            }
        }.joinAll()
    }

}
