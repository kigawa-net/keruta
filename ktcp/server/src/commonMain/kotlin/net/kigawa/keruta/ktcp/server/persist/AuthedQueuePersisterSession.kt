package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowMsg
import net.kigawa.kodel.api.err.Res

interface AuthedQueuePersisterSession {

    fun createQueue(msg: ServerQueueCreateMsg): Res<PersistedQueue, KtcpErr>
    fun getQueues(): Res<List<PersistedQueue>, KtcpErr>
    fun getQueue(input: ServerQueueShowMsg): Res<PersistedQueue, KtcpErr>
}
