package net.kigawa.keruta.ktse.db.table

import org.jetbrains.exposed.sql.Table

object QueueProviderTable: Table("queue_provider") {
    val queueId = integer("queue_id").references(QueueTable.id)
    val providerId = integer("provider_id").references(ProviderTable.id)
    override val primaryKey = PrimaryKey(queueId, providerId)
}
