package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.queue.delete.ServerQueueDeleteEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.delete.ServerQueueDeleteMsg
import net.kigawa.keruta.ktcp.domain.queue.deleted.ClientQueueDeletedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueDeleteEntrypoint : ServerQueueDeleteEntrypoint<ServerCtx> {
    override fun access(
        input: ServerQueueDeleteMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            when (val res = session.persisterSession.queue.deleteQueue(input)) {
                is Res.Err -> return@EntrypointDeferred res.convert()
                is Res.Ok -> Unit
            }
            ctx.server.clientEntrypoints.queueDeleted.access(
                ClientQueueDeletedMsg(id = input.queueId), ctx
            )?.execute() ?: Res.Err(ResponseErr("", null))
        }
    }
}
