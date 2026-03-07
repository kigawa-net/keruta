package net.kigawa.keruta.ktcl.k8s.k8s

import kotlinx.coroutines.*
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.kodel.api.log.LoggerFactory
import kotlin.time.Duration.Companion.seconds

class KerutaK8sClient(
    private val config: K8sConfig,
) {
    private val logger = LoggerFactory.get("KerutaK8sClient")
    private val concurrentCount = 1

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta K8s Client" }
        (0 until concurrentCount).map {
            launch {
                while (isActive){
                    delay(30.seconds)
                }
            }
        }.joinAll()
    }

}
