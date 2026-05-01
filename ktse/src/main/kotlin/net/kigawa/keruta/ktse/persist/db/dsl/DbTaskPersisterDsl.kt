package net.kigawa.keruta.ktse.persist.db.dsl

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.server.persist.PersistedQueue
import net.kigawa.keruta.ktcp.server.persist.PersistedTask
import net.kigawa.keruta.ktcp.server.persist.PersistedUser
import net.kigawa.keruta.ktse.err.NoSingleRecordErr
import net.kigawa.keruta.ktse.persist.db.table.QueueTable
import net.kigawa.keruta.ktse.persist.db.table.QueueUserTable
import net.kigawa.keruta.ktse.persist.db.table.TaskTable
import net.kigawa.keruta.ktse.persist.model.ExposedPersistedTask
import net.kigawa.kodel.api.err.Res
import org.jetbrains.exposed.sql.*

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

    fun updateStatus(user: PersistedUser, taskId: Long, status: String, log: String? = null): Res<PersistedTask, KtcpErr> =
        transaction.run {
            val appendedLog = if (log != null) {
                val existingLog = TaskTable.selectAll()
                    .where { TaskTable.userId eq user.id and (TaskTable.id eq taskId) }
                    .singleOrNull()?.get(TaskTable.log)
                if (existingLog != null) "$existingLog\n$log" else log
            } else null
            TaskTable.update({ TaskTable.userId eq user.id and (TaskTable.id eq taskId) }) {
                it[TaskTable.status] = status
                if (appendedLog != null) it[TaskTable.log] = appendedLog
            }
            TaskTable.selectAll().where {
                TaskTable.userId eq user.id and (TaskTable.id eq taskId)
            }.singleOrNull()
                ?.let { Res.Ok(ExposedPersistedTask(it)) }
                ?: Res.Err(NoSingleRecordErr("", null))
        }

    fun moveTask(user: PersistedUser, taskId: Long, targetQueueId: Long): Res<PersistedTask, KtcpErr> =
        transaction.run {
            val targetQueueExists = QueueUserTable
                .innerJoin(QueueTable)
                .selectAll()
                .where {
                    (QueueTable.id eq targetQueueId) and (QueueUserTable.userId eq user.id)
                }
                .count() > 0

            if (!targetQueueExists) {
                return@run Res.Err(NoSingleRecordErr("Queue not found or access denied", null))
            }

            TaskTable.update({
                (TaskTable.userId eq user.id) and (TaskTable.id eq taskId)
            }) {
                it[queueId] = targetQueueId
            }

            TaskTable.selectAll()
                .where { (TaskTable.userId eq user.id) and (TaskTable.id eq taskId) }
                .singleOrNull()
                ?.let { Res.Ok(ExposedPersistedTask(it)) }
                ?: Res.Err(NoSingleRecordErr("Task not found", null))
        }
}
