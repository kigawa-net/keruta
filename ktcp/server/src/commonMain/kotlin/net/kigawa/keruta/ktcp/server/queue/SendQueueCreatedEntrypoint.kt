package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedArg
import net.kigawa.keruta.ktcp.model.queue.created.ClientQueueCreatedEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueCreatedEntrypoint: ClientQueueCreatedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientQueueCreatedArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input.msg)
            )
            Res.Ok(Unit)
        }
    }
}
