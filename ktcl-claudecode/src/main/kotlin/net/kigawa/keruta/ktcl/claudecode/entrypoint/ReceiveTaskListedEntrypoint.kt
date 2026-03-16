package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcl.claudecode.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcl.claudecode.task.TaskExecutor
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.task.listed.ClientTaskListedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.listed.ClientTaskListedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class ReceiveTaskListedEntrypoint(
    private val taskExecutor: TaskExecutor,
    private val connection: JvmWebSocketConnection,
    private val taskId: Long,
) : ClientTaskListedEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveTaskListedEntrypoint")

    override fun access(
        input: ClientTaskListedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Received ${input.tasks.size} tasks, looking for task $taskId" }

        val task = input.tasks.find { it.id == taskId }
        if (task == null) {
            logger.info { "Task $taskId not found in task list" }
        } else {
            taskExecutor.executeTask(task.id, task.title, task.description, ctx)
        }

        connection.close()
        Res.Ok(Unit)
    }
}
