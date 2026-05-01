package net.kigawa.keruta.ktcl.k8s.k8s

import io.ktor.server.application.*
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.k8s.auth.OidcDiscoveryFetcher
import net.kigawa.keruta.ktcl.k8s.auth.TokenRefresher
import net.kigawa.keruta.ktcl.k8s.config.IdpConfig
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserClaudeConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcp.usecase.client.ProviderTokenCreator
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.net.Url

class K8sModule {
    private val config = K8sConfig.fromEnvironment()
    private val logger = getKogger()

    fun configure(
        application: Application,
        userTokenDao: UserTokenDao,
        userClaudeConfigDao: UserClaudeConfigDao,
        idpConfig: IdpConfig,
        oidcDiscoveryFetcher: OidcDiscoveryFetcher,
        providerTokenCreator: ProviderTokenCreator,
        ktclIssuer: Url,
    ) {
        val tokenRefresher = TokenRefresher(oidcDiscoveryFetcher, idpConfig)
        val client = KerutaK8sClient(config, userTokenDao, userClaudeConfigDao, tokenRefresher, providerTokenCreator, ktclIssuer.toStrUrl())
        application.launch {
            logger.info("Starting K8s client in background")
            client.start()
        }
    }
}
