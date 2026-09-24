package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object QueueUserTable: Table("queue_user") {
    val queueId = long("queue_id").references(QueueTable.id)
    val userId = long("user_id").references(UserTable.id)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(queueId, userId)
}
