package net.kigawa.keruta.ktcl.k8s.k8s

import io.ktor.server.application.*
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.k8s.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.auth.TokenRefresher
import net.kigawa.keruta.ktcl.k8s.config.IdpConfig
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.kodel.api.log.getKogger

class K8sModule {
    private val config = K8sConfig.fromEnvironment()
    private val logger = getKogger()

    fun configure(
        application: Application,
        userTokenDao: UserTokenDao,
        idpConfig: IdpConfig,
        oidcDiscoveryFetcher: OidcDiscoveryFetcher,
    ) {
        val tokenRefresher = TokenRefresher(oidcDiscoveryFetcher, idpConfig)
        val client = KerutaK8sClient(config, userTokenDao, tokenRefresher)
        application.launch {
            logger.info("Starting K8s client in background")
            client.start()
        }
    }
}
