package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface AuthenticatedPersisterSession {
    val task: AuthedTaskPersisterSession
    val queue: AuthedQueuePersisterSession
    fun getProviders(): Res<List<PersistedProvider>, KtcpErr>
}
