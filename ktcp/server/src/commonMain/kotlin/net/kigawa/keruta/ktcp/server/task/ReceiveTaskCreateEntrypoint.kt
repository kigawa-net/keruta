package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateEntrypoint
import net.kigawa.keruta.ktcp.model.task.create.ServerTaskCreateMsg
import net.kigawa.keruta.ktcp.model.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskCreateEntrypoint: ServerTaskCreateEntrypoint<ServerCtx> {
    override fun access(
        input: ServerTaskCreateMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> = EntrypointDeferred {
        val session = ctx.session.authenticated()
            ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
        val task = when (
            val res = session.persisterSession.task.createTask(input)
        ) {
            is Res.Err -> return@EntrypointDeferred res.x()
            is Res.Ok -> res.value
        }
        ctx.server.clientEntrypoints.taskCreated.access(
            ClientTaskCreatedMsg(
                id = task.id,
            ), ctx
        )?.execute() ?: Res.Err(ResponseErr("", null))
    }
}
