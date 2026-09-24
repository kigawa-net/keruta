package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object UserIdpTable: Table("user_idp") {
    val userId = long("user_id").references(UserTable.id)
    val providerId = long("provider_id").references(ProviderTable.id)
    val issuer = varchar("issuer", 50)
    val subject = varchar("subject", 50)
    val audience = varchar("audience", 50)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(issuer, subject, providerId)
}
