package net.kigawa.keruta.ktcl.k8s.persist.dao

import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.keruta.ktcl.k8s.persist.table.UserClaudeConfigTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * ユーザーClaude Code設定Dao
 */
class UserClaudeConfigDao(
    private val dbManager: DbManager,
) {
    /**
     * ユーザーのAnthropic APIキーを保存または更新する
     */
    fun saveOrUpdate(userId: String, anthropicApiKey: String) {
        transaction(dbManager.db) {
            val existing = UserClaudeConfigTable.selectAll().where { UserClaudeConfigTable.userId eq userId }.firstOrNull()
            if (existing != null) {
                UserClaudeConfigTable.update({ UserClaudeConfigTable.userId eq userId }) {
                    it[UserClaudeConfigTable.anthropicApiKey] = anthropicApiKey
                }
            } else {
                UserClaudeConfigTable.insert {
                    it[UserClaudeConfigTable.userId] = userId
                    it[UserClaudeConfigTable.anthropicApiKey] = anthropicApiKey
                }
            }
        }
    }

    /**
     * ユーザーのAnthropic APIキーを取得する
     */
    fun get(userId: String): String? {
        return transaction(dbManager.db) {
            UserClaudeConfigTable.selectAll().where { UserClaudeConfigTable.userId eq userId }
                .firstOrNull()
                ?.get(UserClaudeConfigTable.anthropicApiKey)
        }
    }
}
