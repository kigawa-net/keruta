package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.keruta.ktcp.server.err.ResponseErr
import net.kigawa.keruta.ktcp.server.err.UnauthenticatedErr
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class ReceiveQueueCreateEntrypoint: ServerQueueCreateEntrypoint<ServerCtx> {
    override fun access(
        input: ServerQueueCreateMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, KtcpErr>> {
        return EntrypointDeferred {
            val session = ctx.session.authenticated()
                ?: return@EntrypointDeferred Res.Err(UnauthenticatedErr("", null))
            val queue=when (
                val res = session.persisterSession.createQueue(input)
            ) {
                is Res.Err -> return@EntrypointDeferred res.x()
                is Res.Ok -> res.value
            }
            ctx.server.clientEntrypoints.queueCreated.access(ClientQueueCreatedMsg(
                queueId = queue.id
            ), ctx)?.execute()
                ?: Res.Err(ResponseErr("", null))
        }
    }

}
