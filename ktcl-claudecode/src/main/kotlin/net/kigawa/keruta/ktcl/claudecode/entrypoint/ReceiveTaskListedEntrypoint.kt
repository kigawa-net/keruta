package net.kigawa.keruta.ktcl.claudecode.entrypoint

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
) : ClientTaskListedEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveTaskListedEntrypoint")

    override fun access(
        input: ClientTaskListedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Received ${input.tasks.size} tasks" }

        // statusが"pending"のタスクのみ実行
        input.tasks
            .filter { it.status == "pending" }
            .forEach { task ->
                taskExecutor.executeTask(task.id, task.title, task.description, ctx)
            }

        Res.Ok(Unit)
    }
}
