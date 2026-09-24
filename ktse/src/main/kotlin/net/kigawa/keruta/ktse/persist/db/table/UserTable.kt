package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object UserTable: Table("user") {
    val id = long("id").autoIncrement()
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}
