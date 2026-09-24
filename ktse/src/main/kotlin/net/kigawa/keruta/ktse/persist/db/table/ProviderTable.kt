package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object ProviderTable: Table("provider") {

    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val name = varchar("name", 50)
    val issuer = varchar("issuer", 50)
    val audience = varchar("audience", 50)
    val setting = text("setting")
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}
