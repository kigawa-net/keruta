package net.kigawa.keruta.kise.persist.table

import org.jetbrains.exposed.v1.core.Table

/**
 * プロバイダーテーブル
 */
object ProviderTable : Table("kise_provider") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val name = varchar("name", 50)
    val issuer = varchar("issuer", 255)
    val audience = varchar("audience", 255)
    val setting = text("setting")
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}
