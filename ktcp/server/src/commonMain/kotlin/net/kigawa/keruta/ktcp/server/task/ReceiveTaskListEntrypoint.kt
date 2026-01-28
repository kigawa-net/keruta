package net.kigawa.keruta.ktcp.server.task

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListEntrypoint
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.model.task.listed.ClientTaskListedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveTaskListEntrypoint: ServerTaskListEntrypoint<ServerCtx> {
    override fun access(
        input: ServerTaskListMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            val tasks = when (
                val res = session.persisterSession.task.getTasks(input)
            ) {
                is Res.Ok -> res.value
                is Res.Err -> return@EntrypointDeferred res.x()
            }
            ctx.server.clientEntrypoints.taskListed.access(
                ClientTaskListedMsg(
                    tasks = tasks.map { ClientTaskListedMsg.Task(it.name, it.id) }
                ), ctx
            )?.execute() ?: Res.Err(ResponseErr("", null))
        }
    }

}
