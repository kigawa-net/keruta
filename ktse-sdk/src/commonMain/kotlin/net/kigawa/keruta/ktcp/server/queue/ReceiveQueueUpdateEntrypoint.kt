package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.queue.update.ServerQueueUpdateEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.update.ServerQueueUpdateMsg
import net.kigawa.keruta.ktcp.domain.queue.updated.ClientQueueUpdatedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueUpdateEntrypoint: ServerQueueUpdateEntrypoint<ServerCtx> {
    override fun access(
        input: ServerQueueUpdateMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            val queue = when (
                val res = session.persisterSession.queue.updateQueue(input)
            ) {
                is Res.Err -> return@EntrypointDeferred res.convert()
                is Res.Ok -> res.value
            }
            ctx.server.clientEntrypoints.queueUpdated.access(
                ClientQueueUpdatedMsg(id = queue.id, name = queue.name), ctx
            )?.execute() ?: Res.Err(ResponseErr("", null))
        }
    }
}
