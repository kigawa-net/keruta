package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListMsg
import net.kigawa.keruta.ktcp.server.auth.Idp
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.dump.withStr
import org.jetbrains.exposed.sql.ResultRow

class ExposedPersistedProvider(row: ResultRow): PersistedProvider {
    val issuer: String = row[ProviderTable.issuer]
    val audience = row[ProviderTable.audience]
    val name: String = row[ProviderTable.name]
    val id: Long = row[ProviderTable.id]
    override fun asUserIdp(subject: String): Idp = Idp(audience, subject, issuer)
    override fun asProviderListProvider(): ClientProviderListMsg.Provider = ClientProviderListMsg.Provider(
        name, id, issuer, audience
    )

    val dump
        get() = Dumper.dump(
            this::class,
            ::issuer withStr { it },
            ::audience withStr { it },
            ::name withStr { it },
            ::id withStr { it.toString() },
        )

    override fun toString(): String = dump.str()
}
