package net.kigawa.keruta.ktse.db.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserIdpTable: Table("user_idp") {
    val userId = integer("user_id").references(UserTable.id)
    val issuer = varchar("issuer", 50)
    val subject = varchar("subject", 50)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(issuer, subject)
}
