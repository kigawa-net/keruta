package net.kigawa.keruta.ktcl.k8s.k8s

import io.ktor.server.application.*
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.kodel.api.log.getKogger

class K8sModule(
) {
    val config = K8sConfig.fromEnvironment()
    val client = KerutaK8sClient(config)
    val logger = getKogger()

    fun configure(application: Application) {
        application.launch {
            logger.info("Starting K8s client in background")
            client.start()
        }
    }

}
