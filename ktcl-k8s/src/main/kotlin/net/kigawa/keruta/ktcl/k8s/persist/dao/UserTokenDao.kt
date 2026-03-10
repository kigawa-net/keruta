package net.kigawa.keruta.ktcl.k8s.persist.dao

import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.keruta.ktcl.k8s.persist.table.UserTable
import net.kigawa.keruta.ktcl.k8s.persist.table.UserTokenTable
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


/**
 * ユーザートークンDao（refresh token管理用）
 */
class UserTokenDao(
    private val dbManager: DbManager,
    private val userDao: UserDao,
) {
    val logger = getKogger()
    /**
     * ユーザーのrefresh tokenを保存または更新する
     */
    fun saveOrUpdate(userSubject: String, userIssuer: String, userAudience: String, refreshToken: String) {
        val userId = userDao.findOrCreate(userSubject, userIssuer, userAudience)
        transaction(dbManager.db) {
            val existing = UserTokenTable.selectAll()
                .where { UserTokenTable.userId eq userId }
                .firstOrNull()
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
    fun get(userSubject: String, userIssuer: String): String? {
        val userId = userDao.find(userSubject, userIssuer) ?: return null
        return transaction(dbManager.db) {
            UserTokenTable.selectAll()
                .where { UserTokenTable.userId eq userId }
                .firstOrNull()
                ?.get(UserTokenTable.refreshToken)
        }
    }

    /**
     * DBに保存されている全ユーザーのrefresh tokenを取得する
     */
    fun getRefreshTokens(): List<UserTokenEntry> {
        return transaction(dbManager.db) {
            (UserTokenTable innerJoin UserTable).selectAll()
                .map {
                    UserTokenEntry(
                        userSubject = it[UserTable.userSubject],
                        userIssuer = it[UserTable.userIssuer],
                        userAudience = it[UserTable.userAudience],
                        refreshToken = it[UserTokenTable.refreshToken],
                    )
                }
        }
    }

    /**
     * ユーザーのrefresh tokenを削除する（期限切れ時など）
     */
    fun deleteRefreshToken(userSubject: String, userIssuer: String) {
        val userId = userDao.find(userSubject, userIssuer) ?: return
        transaction(dbManager.db) {
            UserTokenTable.deleteWhere { UserTokenTable.userId eq userId }
        }
    }

    /**
     * ユーザーのgithub tokenを保存または更新する
     */
    fun saveOrUpdateGithubToken(userSubject: String, userIssuer: String, userAudience: String, githubToken: String) {
        val userId = userDao.findOrCreate(userSubject, userIssuer, userAudience)
        transaction(dbManager.db) {
            val existing = UserTokenTable.selectAll()
                .where { UserTokenTable.userId eq userId }
                .firstOrNull()
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
    fun getGithubToken(userSubject: String, userIssuer: String): String? {
        logger.debug { "Getting github token for user $userSubject" }
        val userId = userDao.find(userSubject, userIssuer)
        logger.debug { "User ID: $userId" }
        if (userId == null) {
            logger.warning { "User not found: $userSubject" }
            return null
        }
        return transaction(dbManager.db) {
            UserTokenTable.selectAll()
                .where { UserTokenTable.userId eq userId }
                .firstOrNull()
                .also { logger.debug { "Found user token: $it" } }
                ?.get(UserTokenTable.githubToken)
        }
    }
}
