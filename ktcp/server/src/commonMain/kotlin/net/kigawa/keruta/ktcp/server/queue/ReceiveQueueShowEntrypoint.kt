package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowEntrypoint
import net.kigawa.keruta.ktcp.model.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.model.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueShowEntrypoint: ServerQueueShowEntrypoint<ServerCtx> {
    override fun access(
        input: ServerQueueShowMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            val queues = when (
                val res = session.persisterSession.queue.getQueue(input)
            ) {
                is Res.Ok -> res.value
                is Res.Err -> return@EntrypointDeferred res.convert()
            }
            ctx.server.clientEntrypoints.queueShowed.access(
                ClientQueueShowedMsg(
                    name = queues.name,
                    id = queues.id,
                ), ctx
            )?.execute() ?: Res.Err(ResponseErr("", null))
        }
    }

}
