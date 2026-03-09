package net.kigawa.keruta.ktcl.k8s.persist.dao

import net.kigawa.keruta.ktcl.k8s.persist.db.DbManager
import net.kigawa.keruta.ktcl.k8s.persist.table.UserTable
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * ユーザーDao（userSubject + userIssuer でユーザーを一意管理）
 */
class UserDao(
    private val dbManager: DbManager,
) {
    val logger = getKogger()

    /**
     * ユーザーを検索し、存在しなければ作成してIDを返す
     */
    fun findOrCreate(userSubject: String, userIssuer: String, userAudience: String): Long {
        return transaction(dbManager.db) {
            val existing = UserTable.selectAll()
                .where { (UserTable.userSubject eq userSubject) and (UserTable.userIssuer eq userIssuer) }
                .firstOrNull()
            existing?.get(UserTable.id) ?: UserTable.insert {
                it[UserTable.userSubject] = userSubject
                it[UserTable.userIssuer] = userIssuer
                it[UserTable.userAudience] = userAudience
            }[UserTable.id]
        }
    }

    /**
     * userSubject + userIssuer でユーザーIDを検索する
     */
    fun find(userSubject: String, userIssuer: String): Long? {
        logger.debug { "Finding user $userSubject" }
        return transaction(dbManager.db) {
            UserTable.selectAll()
                .where { (UserTable.userSubject eq userSubject) and (UserTable.userIssuer eq userIssuer) }
                .firstOrNull()
                .also { logger.debug { "Found user ${it?.get(UserTable.id)}" } }
                ?.get(UserTable.id)
        }
    }
}
