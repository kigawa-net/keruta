package net.kigawa.keruta.ktse.persist.kicp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.identity.IdentityId
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRecord
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.keruta.ktse.persist.db.table.KicpRegisterTokenTable
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Suppress("DEPRECATION")
class ExposedRegisterTokenRepository(
    private val dbPersister: DbPersister,
) : RegisterTokenRepository {

    override suspend fun save(record: RegisterTokenRecord): Res<Unit, KicpErr> = withContext(Dispatchers.IO) {
        transaction(dbPersister.db) {
            KicpRegisterTokenTable.insert {
                it[token] = record.token.value
                it[creatorIdentityId] = record.creatorIdentityId.value
                it[expiresAtEpochMs] = record.expiresAtEpochMs
            }
        }
        Res.Ok(Unit)
    }

    override suspend fun find(token: RegisterToken): Res<RegisterTokenRecord?, KicpErr> = withContext(Dispatchers.IO) {
        val row = transaction(dbPersister.db) {
            KicpRegisterTokenTable.selectAll()
                .where { KicpRegisterTokenTable.token eq token.value }
                .singleOrNull()
        }
        Res.Ok(
            row?.let {
                RegisterTokenRecord(
                    token = RegisterToken(it[KicpRegisterTokenTable.token]),
                    creatorIdentityId = IdentityId(it[KicpRegisterTokenTable.creatorIdentityId]),
                    expiresAtEpochMs = it[KicpRegisterTokenTable.expiresAtEpochMs],
                )
            }
        )
    }

    override suspend fun delete(token: RegisterToken): Res<Unit, KicpErr> = withContext(Dispatchers.IO) {
        transaction(dbPersister.db) {
            KicpRegisterTokenTable.deleteWhere { KicpRegisterTokenTable.token eq token.value }
        }
        Res.Ok(Unit)
    }
}
