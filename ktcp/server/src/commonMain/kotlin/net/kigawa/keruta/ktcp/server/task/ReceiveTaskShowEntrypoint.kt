package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowEntrypoint
import net.kigawa.keruta.ktcp.model.task.show.ServerTaskShowMsg
import net.kigawa.keruta.ktcp.model.task.showed.ClientTaskShowedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskShowEntrypoint: ServerTaskShowEntrypoint<ServerCtx> {
    override fun access(
        input: ServerTaskShowMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            val task = when (
                val res = session.persisterSession.task.getTask(input)
            ) {
                is Res.Ok -> res.value
                is Res.Err -> return@EntrypointDeferred res.x()
            }
            ctx.server.clientEntrypoints.taskShowed.access(
                ClientTaskShowedMsg(
                    name = task.name,
                    id = task.id,
                ), ctx
            )?.execute() ?: Res.Err(ResponseErr("", null))
        }
    }

}
