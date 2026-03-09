package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ProviderAddTokenTable : Table("provider_add_token") {
    val token = varchar("token", 36)
    val userId = long("user_id").references(UserTable.id)
    val name = varchar("name", 50)
    val issuer = varchar("issuer", 255)
    val audience = varchar("audience", 50)
    val expiresAt = datetime("expires_at")
    override val primaryKey = PrimaryKey(token)
}
