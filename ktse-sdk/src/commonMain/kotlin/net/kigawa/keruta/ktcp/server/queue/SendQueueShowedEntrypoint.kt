package net.kigawa.keruta.ktcp.server.queue

import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedEntrypoint
import net.kigawa.keruta.ktcp.domain.queue.showed.ClientQueueShowedMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueShowedEntrypoint: ClientQueueShowedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientQueueShowedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}
