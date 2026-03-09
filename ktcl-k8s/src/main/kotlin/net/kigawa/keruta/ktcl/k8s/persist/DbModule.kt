package net.kigawa.keruta.ktcl.k8s.persist

import net.kigawa.keruta.ktcl.k8s.config.AppConfig
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserClaudeConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager

/**
 * データベースモジュール
 */
class DbModule(
    dbManager: DbManager,
) {
    val userDao = UserDao(dbManager)
    val userClaudeConfigDao = UserClaudeConfigDao(dbManager, userDao)
    val userTokenDao = UserTokenDao(dbManager, userDao)

    companion object {
        /**
         * ApplicationConfigからDbModuleを生成
         */
        fun create(appConfig: AppConfig): DbModule {
            val dbManager = DbManager(appConfig.db)
            return DbModule(dbManager)
        }
    }
}


