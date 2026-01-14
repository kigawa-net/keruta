package net.kigawa.keruta.ktse.db.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object TaskTable: Table("task") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id)
    val queueId = integer("queue_id").references(QueueTable.id)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}
