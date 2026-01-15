package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.sql.Table

object QueueProviderTable: Table("queue_provider") {
    val queueId = long("queue_id").references(QueueTable.id)
    val providerId = long("provider_id").references(ProviderTable.id)
    override val primaryKey = PrimaryKey(queueId, providerId)
}
