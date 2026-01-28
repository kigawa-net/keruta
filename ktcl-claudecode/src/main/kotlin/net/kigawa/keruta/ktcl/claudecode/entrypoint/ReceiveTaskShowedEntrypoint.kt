package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcl.claudecode.task.TaskExecutor
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.showed.ClientTaskShowedEntrypoint
import net.kigawa.keruta.ktcp.model.task.showed.ClientTaskShowedMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class ReceiveTaskShowedEntrypoint(
    private val taskExecutor: TaskExecutor,
) : ClientTaskShowedEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveTaskShowedEntrypoint")

    override fun access(
        input: ClientTaskShowedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Task showed: id=${input.id}, title=${input.title}" }

        // ClientTaskShowedMsgにはstatusフィールドがないため、常に実行
        taskExecutor.executeTask(input.id, input.title, input.description, ctx)
    }
}