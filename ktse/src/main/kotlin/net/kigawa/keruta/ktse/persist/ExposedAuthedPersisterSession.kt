package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedVerifyTables
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

class ExposedAuthedPersisterSession(
    override val verifyTables: PersistedVerifyTables, val dbPersister: DbPersister,
): AuthenticatedPersisterSession {
    override val task by lazy { ExposedAuthedTaskPersisterSession(this) }
    override val queue by lazy { ExposedAuthedQueuePersisterSession(this) }

    override fun getProviders(): Res<List<PersistedProvider>, KtcpErr> = dbPersister.execTransaction {
        it.provider.getAll(verifyTables.user)
    }

    override fun deleteProvider(id: Long): Res<Unit, KtcpErr> = dbPersister.execTransaction {
        it.provider.delete(verifyTables.user, id)
    }
}
