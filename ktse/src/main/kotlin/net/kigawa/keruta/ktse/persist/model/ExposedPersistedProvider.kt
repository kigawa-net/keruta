package net.kigawa.keruta.ktse.persist.model

import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedMsg
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import net.kigawa.kodel.api.dump.Dumper
import net.kigawa.kodel.api.dump.withStr
import net.kigawa.kodel.api.net.Url
import org.jetbrains.exposed.sql.ResultRow

data class IdpData(val issuer: String, val subject: String, val audience: String)

class ExposedPersistedProvider(row: ResultRow, private val idps: List<IdpData> = emptyList()): PersistedProvider {
    override val issuer: Url = Url.parse(row[ProviderTable.issuer])
    override val audience = row[ProviderTable.audience]
    override val name: String = row[ProviderTable.name]
    override val id: Long = row[ProviderTable.id]
    override fun asProviderListProvider(): ClientProviderListedMsg.Provider = ClientProviderListedMsg.Provider(
        name, id, issuer.toStrUrl(), audience,
        idps.map { ClientProviderListedMsg.Idp(it.issuer, it.subject, it.audience) }
    )

    override fun asProviderCreatedProvider(): ClientProviderCreatedMsg.Provider = ClientProviderCreatedMsg.Provider(
        id, name, issuer.toStrUrl(), audience
    )

    override fun toString(): String = Dumper.dump(
        this::class,
        ::issuer withStr { it.toStrUrl() },
        ::audience withStr { it },
        ::name withStr { it },
        ::id withStr { it.toString() },
    ).str()
}
