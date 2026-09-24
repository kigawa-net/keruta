package net.kigawa.keruta.ktse.persist.db.table

import org.jetbrains.exposed.v1.core.Table

object KicpRegisterTokenTable : Table("kicp_register_token") {
    val token = varchar("token", 255)
    val creatorIdentityId = varchar("creator_identity_id", 512)
    val expiresAtEpochMs = long("expires_at_epoch_ms")
    override val primaryKey = PrimaryKey(token)
}
