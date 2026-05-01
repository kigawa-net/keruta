package net.kigawa.keruta.ktcl.k8s.persist.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * ユーザートークンテーブル（refresh token永続化用）
 */
object UserTokenTable : Table("user_token") {
    val id = long("id").autoIncrement()
    val userId = reference("user_id", UserTable.id)
    val refreshToken = text("refresh_token")
    val githubToken = text("github_token").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId)
    }
}
