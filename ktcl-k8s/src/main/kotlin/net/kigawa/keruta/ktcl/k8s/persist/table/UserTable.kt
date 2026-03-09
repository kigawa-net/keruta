package net.kigawa.keruta.ktcl.k8s.persist.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * ユーザーテーブル（userSubject + userIssuer でユーザーを一意識別）
 */
object UserTable : Table("user") {
    val id = long("id").autoIncrement()
    val userSubject = varchar("user_subject", 255)
    val userIssuer = varchar("user_issuer", 512)
    val userAudience = varchar("user_audience", 512)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userSubject, userIssuer)
    }
}
