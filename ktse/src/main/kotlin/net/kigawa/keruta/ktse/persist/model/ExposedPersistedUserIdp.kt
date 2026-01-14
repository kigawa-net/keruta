package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.auth.UserIdp
import net.kigawa.keruta.ktcp.server.persist.PersistedUserIdp
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedUserIdp(row: ResultRow): PersistedUserIdp {
    override val subject: String = row[UserIdpTable.subject]
    val issuer: String = row[UserIdpTable.issuer]
    val audience: String = row[UserIdpTable.audience]

    override fun asUserIdp(): UserIdp {
        return UserIdp(audience, subject, issuer)
    }
}
