package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktcp.server.persist.PersistedTask
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.TaskTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedTask
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class DbTaskPersisterDsl(val transaction: Transaction) {

    fun create(
        user: PersistedUser, queue: PersistedQueue, task: ServerTaskCreateMsg,
    ): Res<PersistedTask, KtcpErr> =
        transaction.run {
            val queue = TaskTable.insert {
                it[TaskTable.userId] = user.id
                it[TaskTable.queueId] = queue.id
                it[TaskTable.title] = task.title
                it[TaskTable.description] = task.description
            }.resultedValues?.singleOrNull() ?: return Res.Err(NoSingleRecordErr("", null))
            return Res.Ok(ExposedPersistedTask(queue))
        }

    fun getAll(user: PersistedUser, queue: PersistedQueue): Res<List<PersistedTask>, KtcpErr> = transaction.run {
        TaskTable.selectAll().where { TaskTable.userId eq user.id and (TaskTable.queueId eq queue.id) }
            .map { ExposedPersistedTask(it) }
            .let { Res.Ok(it) }
    }

    fun findByUserQueueAndId(user: PersistedUser, queue: PersistedQueue, id: Long): Res<PersistedTask, KtcpErr> =
        transaction.run {
            TaskTable.selectAll().where {
                TaskTable.userId eq user.id and (TaskTable.queueId eq queue.id) and (TaskTable.id eq id)
            }.singleOrNull()
                ?.let { Res.Ok(ExposedPersistedTask(it)) }
                ?: Res.Err(NoSingleRecordErr("", null))
        }

    fun updateStatus(user: PersistedUser, taskId: Long, status: String): Res<PersistedTask, KtcpErr> =
        transaction.run {
            TaskTable.update({ TaskTable.userId eq user.id and (TaskTable.id eq taskId) }) {
                it[TaskTable.status] = status
            }
            TaskTable.selectAll().where {
                TaskTable.userId eq user.id and (TaskTable.id eq taskId)
            }.singleOrNull()
                ?.let { Res.Ok(ExposedPersistedTask(it)) }
                ?: Res.Err(NoSingleRecordErr("", null))
        }
}
