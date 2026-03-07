package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface AuthenticatedPersisterSession {
    val verifyTables: PersistedVerifyTables
    val task: AuthedTaskPersisterSession
    val queue: AuthedQueuePersisterSession
    fun getProviders(): Res<List<PersistedProvider>, KtcpErr>
    fun deleteProvider(id: Long): Res<Unit, KtcpErr>
}
