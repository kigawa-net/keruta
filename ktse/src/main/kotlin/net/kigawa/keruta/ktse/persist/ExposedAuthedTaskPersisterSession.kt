package net.kigawa.keruta.ktse.persist

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.domain.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.server.persist.AuthedTaskPersisterSession
import net.kigawa.keruta.ktcp.server.persist.PersistedTask
import net.kigawa.kodel.api.err.Res

class ExposedAuthedTaskPersisterSession(
    val session: ExposedAuthedPersisterSession,
): AuthedTaskPersisterSession {
    val dbPersister by session::dbPersister
    val verifyTables by session::verifyTables
    override fun createTask(
        task: ServerTaskCreateMsg,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        val queue = when (
            val res = it.queue.findByUserAndId(verifyTables.user, task.queueId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        it.task.create(verifyTables.user, queue, task)
    }

    override fun getTasks(input: ServerTaskListMsg): Res<List<PersistedTask>, KtcpErr> = dbPersister.execTransaction {
        val queue = when (
            val res = it.queue.findByUserAndId(verifyTables.user, input.queueId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        it.task.getAll(verifyTables.user, queue)
    }

    override fun getTask(
        input: ServerTaskShowMsg,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        val queue = when (
            val res = it.queue.findByUserAndId(verifyTables.user, input.queueId)
        ) {
            is Res.Err -> return@execTransaction res.convert()
            is Res.Ok -> res.value
        }
        it.task.findByUserQueueAndId(verifyTables.user, queue, input.id)
    }

    override fun updateTaskStatus(
        taskId: Long,
        status: String,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        it.task.updateStatus(verifyTables.user, taskId, status)
    }

    override fun moveTask(
        taskId: Long,
        targetQueueId: Long,
    ): Res<PersistedTask, KtcpErr> = dbPersister.execTransaction {
        it.task.moveTask(verifyTables.user, taskId, targetQueueId)
    }
}
