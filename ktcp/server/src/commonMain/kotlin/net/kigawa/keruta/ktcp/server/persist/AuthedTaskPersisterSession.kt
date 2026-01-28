package net.kigawa.keruta.ktcp.server.persist

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.kodel.api.err.Res

interface AuthedTaskPersisterSession {
    fun createTask(task: ServerTaskCreateMsg): Res<PersistedTask, KtcpErr>
    fun getTasks(input: ServerTaskListMsg): Res<List<PersistedTask>, KtcpErr>
    fun getTask(input: ServerTaskShowMsg): Res<PersistedTask, KtcpErr>
    fun updateTaskStatus(taskId: Long, status: String): Res<PersistedTask, KtcpErr>
    fun moveTask(taskId: Long, targetQueueId: Long): Res<PersistedTask, KtcpErr>
}
