package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object QueueProviderTable: Table("queue_provider") {
    val queueId = long("queue_id").references(QueueTable.id)
    val providerId = long("provider_id").references(ProviderTable.id)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(queueId, providerId)
}
