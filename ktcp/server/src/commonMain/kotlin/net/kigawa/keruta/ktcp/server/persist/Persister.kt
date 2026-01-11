package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res

interface Persister {
    suspend fun createTask(taskToCreate: TaskToCreate): Res<Unit, KtcpErr>
}
