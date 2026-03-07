package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.domain.queue.update.ServerQueueUpdateMsg
import net.kigawa.keruta.ktcp.server.persist.AuthedQueuePersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktcp.server.queue.QueueToCreate
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.kodel.api.err.Res

class ExposedAuthedQueuePersisterSession(
    session: ExposedAuthedPersisterSession,
): AuthedQueuePersisterSession {

    val dbPersister by session::dbPersister
    val verifyTables by session::verifyTables
    override fun createQueue(
        msg: ServerQueueCreateMsg,
    ): Res<PersistedQueue, KtcpErr> = dbPersister.execTransaction {
        val provider = when (
            val res = it.provider.findByUserAndId(verifyTables.user, msg.providerId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
            null -> return@execTransaction Res.Err(NoSingleRecordErr("", null))
        }
        val queue = when (
            val res = it.queue.createQueue(
                QueueToCreate(msg), provider, verifyTables.user
            )
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        Res.Ok(queue)
    }

    override fun getQueues(): Res<List<PersistedQueue>, KtcpErr> = dbPersister.execTransaction {
        it.queue.getAll(verifyTables.user)
    }

    override fun getQueue(
        input: ServerQueueShowMsg,
    ): Res<PersistedQueue, KtcpErr> = dbPersister.execTransaction {
        it.queue.findByUserAndId(verifyTables.user, input.id)
    }

    override fun updateQueue(
        msg: ServerQueueUpdateMsg,
    ): Res<PersistedQueue, KtcpErr> = dbPersister.execTransaction {
        it.queue.updateQueue(verifyTables.user, msg.queueId, msg.name)
    }
}
