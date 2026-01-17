package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.persist.TaskToCreate
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

class KtseAuthenticatedPersisterSession(
    val user: PersistedUser, val provider: PersistedProvider, val dbPersister: DbPersister,
): AuthenticatedPersisterSession {
    override suspend fun createTask(
        task: TaskToCreate,
    ): Res<Unit, KtcpErr> {
        TODO()
    }

    override suspend fun getProviders(): Res<List<PersistedProvider>, KtcpErr> = dbPersister.execTransaction {
        it.getProviders(user)
    }
}
