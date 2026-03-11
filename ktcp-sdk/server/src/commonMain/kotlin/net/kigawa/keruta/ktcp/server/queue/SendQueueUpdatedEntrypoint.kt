package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.domain.queue.updated.ClientQueueUpdatedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.updated.ClientQueueUpdatedMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueUpdatedEntrypoint: ClientQueueUpdatedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientQueueUpdatedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer.serialize(input))
            Res.Ok(Unit)
        }
    }
}
