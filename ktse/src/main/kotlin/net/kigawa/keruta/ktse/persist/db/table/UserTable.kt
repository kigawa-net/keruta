package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable: Table("user") {
    val id = integer("id").autoIncrement()
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}
