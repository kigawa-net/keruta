package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object QueueTable: Table("queue") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50)
    val providerId = long("provider_id").references(ProviderTable.id)
    val setting = text("setting").nullable().default("")
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}
