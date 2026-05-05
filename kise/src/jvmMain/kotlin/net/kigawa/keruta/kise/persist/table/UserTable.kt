package net.kigawa.keruta.kise.persist.table

import org.jetbrains.exposed.v1.core.Table

/**
 * ユーザーテーブル
 */
object UserTable : Table("kise_user") {
    val id = long("id").autoIncrement()
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}
