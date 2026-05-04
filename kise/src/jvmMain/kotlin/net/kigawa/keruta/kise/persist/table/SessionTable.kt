package net.kigawa.keruta.kise.persist.table

import org.jetbrains.exposed.v1.core.Table

/**
 * セッションンテーブル
 */
object SessionTable : Table("kise_session") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val token = varchar("token", 512)
    val expiresAt = long("expires_at")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    override val primaryKey = PrimaryKey(id)
}