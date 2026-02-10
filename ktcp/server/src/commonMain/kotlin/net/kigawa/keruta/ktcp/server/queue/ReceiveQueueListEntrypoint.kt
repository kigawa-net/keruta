package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListEntrypoint
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.model.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueListEntrypoint: ServerQueueListEntrypoint<ServerCtx> {
    override fun access(
        input: ServerQueueListMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            val queues = when (
                val res = session.persisterSession.queue.getQueues()
            ) {
                is Res.Ok -> res.value
                is Res.Err -> return@EntrypointDeferred res.convert()
            }
            ctx.server.clientEntrypoints.queueListed.access(
                ClientQueueListedMsg(
                queues = queues.map { ClientQueueListedMsg.Queue(it.name, it.id) }
            ), ctx)?.execute() ?: Res.Err(ResponseErr("", null))
        }
    }

}
