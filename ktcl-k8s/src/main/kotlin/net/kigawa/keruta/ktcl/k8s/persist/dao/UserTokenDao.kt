package net.kigawa.keruta.ktcl.k8s.persist.dao

import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.keruta.ktcl.k8s.persist.table.UserTokenTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


/**
 * ユーザートークンDao（refresh token管理用）
 */
class UserTokenDao(
    private val dbManager: DbManager,
) {
    /**
     * ユーザーのrefresh tokenを保存または更新する
     */
    fun saveOrUpdate(userId: String, refreshToken: String) {
        transaction(dbManager.db) {
            val existing = UserTokenTable.selectAll().where { UserTokenTable.userId eq userId }.firstOrNull()
            if (existing != null) {
                UserTokenTable.update({ UserTokenTable.userId eq userId }) {
                    it[UserTokenTable.refreshToken] = refreshToken
                }
            } else {
                UserTokenTable.insert {
                    it[UserTokenTable.userId] = userId
                    it[UserTokenTable.refreshToken] = refreshToken
                }
            }
        }
    }

    /**
     * ユーザーのrefresh tokenを取得する
     */
    fun get(userId: String): String? {
        return transaction(dbManager.db) {
            UserTokenTable.selectAll().where { UserTokenTable.userId eq userId }
                .firstOrNull()
                ?.get(UserTokenTable.refreshToken)
        }
    }

    /**
     * DBに保存されている最初のユーザーのrefresh tokenを取得する
     */
    fun getRefreshTokens(): List<Pair<String, String>> {
        return transaction(dbManager.db) {
            UserTokenTable.selectAll()
                .map { it[UserTokenTable.userId] to it[UserTokenTable.refreshToken] }
        }
    }

    /**
     * ユーザーのgithub tokenを保存または更新する
     */
    fun saveOrUpdateGithubToken(userId: String, githubToken: String) {
        transaction(dbManager.db) {
            val existing = UserTokenTable.selectAll().where { UserTokenTable.userId eq userId }.firstOrNull()
            if (existing != null) {
                UserTokenTable.update({ UserTokenTable.userId eq userId }) {
                    it[UserTokenTable.githubToken] = githubToken
                }
            } else {
                UserTokenTable.insert {
                    it[UserTokenTable.userId] = userId
                    it[UserTokenTable.githubToken] = githubToken
                }
            }
        }
    }

    /**
     * ユーザーのgithub tokenを取得する
     */
    fun getGithubToken(userId: String): String? {
        return transaction(dbManager.db) {
            UserTokenTable.selectAll().where { UserTokenTable.userId eq userId }
                .firstOrNull()
                ?.get(UserTokenTable.githubToken)
        }
    }
}
