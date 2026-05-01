package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.domain.queue.deleted.ClientQueueDeletedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.deleted.ClientQueueDeletedMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueDeletedEntrypoint : ClientQueueDeletedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientQueueDeletedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer.serialize(input))
            Res.Ok(Unit)
        }
    }
}
