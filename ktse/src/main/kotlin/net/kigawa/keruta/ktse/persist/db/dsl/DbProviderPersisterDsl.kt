package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktse.err.MultipleRecordErr
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.ProviderTable
import net.kigawa.keruta.ktse.persist.db.table.QueueProviderTable
import net.kigawa.keruta.ktse.persist.db.table.UserIdpTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedProvider
import net.kigawa.keruta.ktse.persist.model.IdpData
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DbProviderPersisterDsl(
    val transaction: Transaction,
) {

    fun getAll(user: PersistedUser): Res<List<PersistedProvider>, KtcpErr> = Res.Ok(
        (ProviderTable leftJoin UserIdpTable)
            .selectAll()
            .where { ProviderTable.userId eq user.id }
            .groupBy { it[ProviderTable.id] }
            .map { (_, rows) ->
                val row = rows.first()
                val idps = rows.mapNotNull { idpRow ->
                    val issuer = idpRow.getOrNull(UserIdpTable.issuer) ?: return@mapNotNull null
                    IdpData(
                        issuer = issuer,
                        subject = idpRow[UserIdpTable.subject],
                        audience = idpRow[UserIdpTable.audience],
                    )
                }
                ExposedPersistedProvider(row, idps)
            }
    )

    fun findByUserAndId(user: PersistedUser, id: Long): Res<PersistedProvider, KtcpErr>? = transaction.run {
        val res = ProviderTable.selectAll().where {
            ProviderTable.userId eq user.id and (ProviderTable.id eq id)
        }
        if (res.empty()) return@run null
        res.singleOrNull()?.let { return@run Res.Ok(ExposedPersistedProvider(it)) }
        return@run Res.Err(MultipleRecordErr("", null))
    }

    fun delete(user: PersistedUser, id: Long): Res<Unit, KtcpErr> {
        val exists = ProviderTable.selectAll().where {
            ProviderTable.userId eq user.id and (ProviderTable.id eq id)
        }.count() > 0
        if (!exists) return Res.Err(NoSingleRecordErr("provider not found: $id", null))

        UserIdpTable.deleteWhere { providerId eq id }
        QueueProviderTable.deleteWhere { providerId eq id }
        ProviderTable.deleteWhere { ProviderTable.id eq id }
        return Res.Ok(Unit)
    }

}
