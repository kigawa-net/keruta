package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.server.persist.AuthenticatedPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedProvider
import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktcp.server.queue.QueueToCreate
import net.kigawa.keruta.ktcp.server.task.TaskToCreate
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.DbPersister
import net.kigawa.kodel.api.err.Res

class KtseAuthenticatedPersisterSession(
    val user: PersistedUser, val provider: PersistedProvider, val dbPersister: DbPersister,
): AuthenticatedPersisterSession {
    override fun createTask(
        task: TaskToCreate,
    ): Res<Unit, KtcpErr> {
        TODO()
    }

    override fun getProviders(): Res<List<PersistedProvider>, KtcpErr> = dbPersister.execTransaction {
        it.provider.getAll(user)
    }

    override fun createQueue(
        msg: ServerQueueCreateMsg,
    ): Res<PersistedQueue, KtcpErr> = dbPersister.execTransaction {
        val provider = when (
            val res = it.provider.findByUserAndId(user, msg.providerId)
        ) {
            is Res.Err -> return@execTransaction res.x()
            is Res.Ok -> res.value
            null -> return@execTransaction Res.Err(NoSingleRecordErr("", null))
        }
        val queue = when (
            val res = it.queue.createQueue(
                QueueToCreate(msg), provider, user
            )
        ) {
            is Res.Err -> return@execTransaction res.x()
            is Res.Ok -> res.value
        }
        Res.Ok(queue)
    }

    override fun getQueues(): Res<List<PersistedQueue>, KtcpErr> = dbPersister.execTransaction {
        it.queue.getAll(user)
    }

    override fun getQueue(
        input: ServerQueueShowMsg,
    ): Res<PersistedQueue, KtcpErr> = dbPersister.execTransaction {
        it.queue.findByUserAndId(user, input.id)
    }
}
