package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateEntrypoint
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.keruta.ktcp.model.task.updated.ClientTaskUpdatedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskUpdateEntrypoint: ServerTaskUpdateEntrypoint<ServerCtx> {
    override fun access(
        input: ServerTaskUpdateMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        val session = ctx.session.authenticated()
            ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
        val task = when (
            val res = session.persisterSession.task.updateTaskStatus(input.taskId, input.status)
        ) {
            is Res.Err -> return@EntrypointDeferred res.x()
            is Res.Ok -> res.value
        }
        ctx.server.clientEntrypoints.taskUpdated.access(
            ClientTaskUpdatedMsg(
                id = task.id,
                status = task.status,
            ), ctx
        )?.execute() ?: Res.Err(ResponseErr("", null))
    }
}