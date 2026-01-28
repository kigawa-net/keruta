package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.move.ServerTaskMoveEntrypoint
import net.kigawa.keruta.ktcp.model.task.move.ServerTaskMoveMsg
import net.kigawa.keruta.ktcp.model.task.moved.ClientTaskMovedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskMoveEntrypoint: ServerTaskMoveEntrypoint<ServerCtx> {
    override fun access(
        input: ServerTaskMoveMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        val session = ctx.session.authenticated()
            ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
        val task = when (
            val res = session.persisterSession.task.moveTask(input.taskId, input.targetQueueId)
        ) {
            is Res.Err -> return@EntrypointDeferred res.x()
            is Res.Ok -> res.value
        }
        ctx.server.clientEntrypoints.taskMoved.access(
            ClientTaskMovedMsg(
                taskId = task.id,
                queueId = task.queueId,
            ), ctx
        )?.execute() ?: Res.Err(ResponseErr("", null))
    }
}