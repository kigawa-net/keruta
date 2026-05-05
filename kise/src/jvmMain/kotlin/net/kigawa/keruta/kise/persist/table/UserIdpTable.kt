package net.kigawa.keruta.kise.persist.table

import org.jetbrains.exposed.v1.core.Table

/**
 * ユーザーIdP関連付けテーブル
 */
object UserIdpTable : Table("kise_user_idp") {
    val userId = long("user_id").references(UserTable.id)
    val providerId = long("provider_id").references(ProviderTable.id)
    val issuer = varchar("issuer", 255)
    val subject = varchar("subject", 255)
    val audience = varchar("audience", 255)
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(userId, issuer, subject)
}
