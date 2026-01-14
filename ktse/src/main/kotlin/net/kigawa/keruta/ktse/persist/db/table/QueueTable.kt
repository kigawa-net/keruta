package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object QueueTable: Table("queue") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val providerId = integer("provider_id").references(ProviderTable.id)
    val setting = text("setting")
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}
