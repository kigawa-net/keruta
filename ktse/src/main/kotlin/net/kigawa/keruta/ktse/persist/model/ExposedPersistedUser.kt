package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktse.persist.db.table.UserTable
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedUser(
    row: ResultRow, override val currentIdp: PersistedUserIdp,
): PersistedUser {
    override val id: Long = row[UserTable.id]
}
