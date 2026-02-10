package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.server.persist.AuthedTaskPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedTask
import net.kigawa.kodel.api.err.Res

class ExposedAuthedTaskPersisterSession(
    val session: ExposedAuthedPersisterSession,
): AuthedTaskPersisterSession {
    val dbPersister by session::dbPersister
    val user by session::user
    override fun createTask(
        task: ServerTaskCreateMsg,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        val queue = when (
            val res = it.queue.findByUserAndId(user, task.queueId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        it.task.create(user, queue,task)
    }

    override fun getTasks(input: ServerTaskListMsg): Res<List<PersistedTask>, KtcpErr> = dbPersister.execTransaction {
        val queue = when (
            val res = it.queue.findByUserAndId(user, input.queueId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        it.task.getAll(user, queue)
    }

    override fun getTask(
        input: ServerTaskShowMsg,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        val queue = when (
            val res = it.queue.findByUserAndId(user, input.queueId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        it.task.findByUserQueueAndId(user, queue, input.id)
    }

    override fun updateTaskStatus(
        taskId: Long,
        status: String,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        it.task.updateStatus(user, taskId, status)
    }

    override fun moveTask(
        taskId: Long,
        targetQueueId: Long,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        it.task.moveTask(user, taskId, targetQueueId)
    }
}
