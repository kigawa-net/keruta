package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.server.auth.UserIdp
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedProvider(row: ResultRow): PersistedProvider {
    val issuer: String = row[ProviderTable.issuer]
    val audience = row[ProviderTable.audience]
    override fun asUserIdp(subject: String): UserIdp = UserIdp(audience, subject, issuer)
}
