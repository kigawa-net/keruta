package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object TaskTable: Table("task") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val queueId = long("queue_id").references(QueueTable.id)
    val createdAt = datetime("created_at")
    val title = varchar("title", 255)
    val description = text("description")
    override val primaryKey = PrimaryKey(id)
}
