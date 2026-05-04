package net.kigawa.keruta.ktcl.k8s.persist.dao

import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.keruta.ktcl.k8s.persist.table.UserClaudeConfigTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

/**
 * ユーザーClaude Code設定Dao
 */
class UserClaudeConfigDao(
    private val dbManager: DbManager,
    private val userDao: UserDao,
) {
    /**
     * ユーザーのAnthropic APIキーを保存または更新する
     */
    fun saveOrUpdate(userId: String, userIssuer: String, userAudience: String, anthropicApiKey: String) {
        val dbUserId = userDao.findOrCreate(userId, userIssuer, userAudience)
        transaction(dbManager.db) {
            val existing = UserClaudeConfigTable.selectAll()
                .where { UserClaudeConfigTable.userId eq dbUserId }
                .firstOrNull()
            if (existing != null) {
                UserClaudeConfigTable.update({ UserClaudeConfigTable.userId eq dbUserId }) {
                    it[UserClaudeConfigTable.anthropicApiKey] = anthropicApiKey
                }
            } else {
                UserClaudeConfigTable.insert {
                    it[UserClaudeConfigTable.userId] = dbUserId
                    it[UserClaudeConfigTable.anthropicApiKey] = anthropicApiKey
                }
            }
        }
    }

    /**
     * ユーザーのAnthropic APIキーを取得する
     */
    fun get(userId: String, userIssuer: String): String? {
        val dbUserId = userDao.find(userId, userIssuer) ?: return null
        return transaction(dbManager.db) {
            UserClaudeConfigTable.selectAll()
                .where { UserClaudeConfigTable.userId eq dbUserId }
                .firstOrNull()
                ?.get(UserClaudeConfigTable.anthropicApiKey)
        }
    }
}
