package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcl.claudecode.task.TaskExecutor
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedEntrypoint
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class ReceiveTaskShowedEntrypoint(
    private val taskExecutor: TaskExecutor,
    private val taskId: Long,
) : ClientTaskShowedEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveTaskShowedEntrypoint")

    override fun access(
        input: ClientTaskShowedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Task showed: id=${input.id}, title=${input.title}" }

        if (input.id != taskId) {
            logger.info { "Task ${input.id} is not the assigned task $taskId, skipping" }
            return@EntrypointDeferred Res.Ok(Unit)
        }
        taskExecutor.executeTask(input.id, input.title, input.description, ctx)
    }
}
