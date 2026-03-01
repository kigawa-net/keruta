package net.kigawa.keruta.ktcl.k8s.persist.dao

import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.keruta.ktcl.k8s.persist.table.K8sConfigTable
import net.kigawa.keruta.ktcl.k8s.persist.table.QueueConfigTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Kubernetes設定Dao
 */
class K8sConfigDao(
    private val dbManager: DbManager,
) {
    /**
     * 設定を取得する
     */
    fun get(configKey: String): String? {
        return transaction(dbManager.db) {
            K8sConfigTable.select(K8sConfigTable.configKey eq configKey)
                .firstOrNull()
                ?.get(K8sConfigTable.configValue)
        }
    }

}

/**
 * キュー設定Dao
 */
class QueueConfigDao(
    private val dbManager: DbManager,
) {
    /**
     * 設定を取得する
     */
    fun get(configKey: String): String? {
        return transaction(dbManager.db) {
            QueueConfigTable.select(QueueConfigTable.configKey eq configKey)
                .firstOrNull()
                ?.get(QueueConfigTable.configValue)
        }
    }

}
