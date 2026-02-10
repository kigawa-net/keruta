package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import net.kigawa.kodel.api.net.Url
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedUserIdp(row: ResultRow): PersistedUserIdp {
    override val userId: Long = row[UserIdpTable.userId]
    override val subject: String = row[UserIdpTable.subject]
    override val issuer: Url = Url.parse(row[UserIdpTable.issuer])
    override val audience: String = row[UserIdpTable.audience]

}
