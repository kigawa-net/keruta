package net.kigawa.keruta.ktcl.k8s.persist

import io.ktor.server.application.*
import io.ktor.util.*
import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.persist.dao.K8sConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.QueueConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.kodel.api.log.getKogger

/**
 * データベースモジュール
 */
class DbModule(
    dbManager: DbManager,
) {
    private val logger = getKogger()

    val k8sConfigDao = K8sConfigDao(dbManager)
    val queueConfigDao = QueueConfigDao(dbManager)
    val userTokenDao = UserTokenDao(dbManager)

    companion object {
        /**
         * ApplicationConfigからDbModuleを生成
         */
        fun create(applicationConfig: io.ktor.server.config.ApplicationConfig): DbModule {
            val appConfig = AppConfig.load(applicationConfig)
            val dbManager = DbManager(appConfig.db)
            return DbModule(dbManager)
        }
    }

    fun configure(application: Application) {
        logger.info("Configuring database module")
        // KtorのDependency Injectionに登録
        application.attributes.put(K8S_CONFIG_DAO_KEY, k8sConfigDao)
        application.attributes.put(QUEUE_CONFIG_DAO_KEY, queueConfigDao)
        application.attributes.put(USER_TOKEN_DAO_KEY, userTokenDao)
        logger.info("Database module configured successfully")
    }
}

/**
 * K8s設定DaoのAttributeKey
 */
val K8S_CONFIG_DAO_KEY = AttributeKey<K8sConfigDao>("k8sConfigDao")

/**
 * キュー設定DaoのAttributeKey
 */
val QUEUE_CONFIG_DAO_KEY = AttributeKey<QueueConfigDao>("queueConfigDao")

/**
 * ユーザートークンDaoのAttributeKey
 */
val USER_TOKEN_DAO_KEY = AttributeKey<UserTokenDao>("userTokenDao")
