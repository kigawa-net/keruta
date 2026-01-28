package net.kigawa.keruta.ktcl.claudecode.entrypoint

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.created.ClientTaskCreatedEntrypoint
import net.kigawa.keruta.ktcp.model.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class ReceiveTaskCreatedEntrypoint(
    private val ktcpClient: KtcpClient,
    private val queueId: Long,
) : ClientTaskCreatedEntrypoint<ClientCtx> {
    private val logger = LoggerFactory.get("ReceiveTaskCreatedEntrypoint")

    override fun access(
        input: ClientTaskCreatedMsg,
        ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        logger.info { "Task created: id=${input.id}" }

        // タスク詳細を取得
        ktcpClient.ktcpServerEntrypoints.taskShow.access(
            ServerTaskShowMsg(queueId = queueId, id = input.id),
            ctx
        )?.execute() ?: Res.Ok(Unit)
    }
}