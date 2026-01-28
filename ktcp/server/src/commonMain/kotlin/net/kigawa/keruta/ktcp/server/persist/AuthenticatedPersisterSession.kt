package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.server.task.TaskToCreate
import net.kigawa.kodel.api.err.Res

interface AuthenticatedPersisterSession {
    suspend fun createTask(task: TaskToCreate): Res<Unit, KtcpErr>
    suspend fun getProviders(): Res<List<PersistedProvider>, KtcpErr>
    fun createQueue(msg: ServerQueueCreateMsg): Res<PersistedQueue, KtcpErr>
}
