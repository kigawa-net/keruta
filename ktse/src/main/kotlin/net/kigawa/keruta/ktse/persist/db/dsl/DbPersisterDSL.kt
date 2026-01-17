package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.auth.ProviderIdpConfig
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktse.err.MultipleRecordErr
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedProvider
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class DbPersisterDSL(val transaction: Transaction) {
    val user = DbUserPersisterDsl(transaction)

    fun getProviders(user: PersistedUser): Res<List<PersistedProvider>, KtcpErr> = Res.Ok(
        ProviderTable.selectAll()
            .where { ProviderTable.userId eq user.id }
            .map { ExposedPersistedProvider(it) }
    )

    fun getProviderOrNull(issuer: String, id: Long): Res<PersistedProvider, KtcpErr>? = transaction.run {
        val res = ProviderTable.selectAll().where {
            ProviderTable.issuer eq issuer and (ProviderTable.userId eq id)
        }
        if (res.empty()) return@run null
        res.singleOrNull()?.let { return@run Res.Ok(ExposedPersistedProvider(it)) }
        return@run Res.Err(MultipleRecordErr("", null))
    }

    fun createProvider(idp: ProviderIdpConfig, user: PersistedUser): Res<PersistedProvider, KtcpErr> {
        val provider = ProviderTable.insert {
            it[ProviderTable.issuer] = idp.issuer
            it[ProviderTable.audience] = idp.audience
            it[ProviderTable.userId] = user.id
            it[ProviderTable.name] = idp.name
        }.resultedValues?.singleOrNull() ?: return Res.Err(NoSingleRecordErr("", null))
        return Res.Ok(ExposedPersistedProvider(provider))
    }

}
