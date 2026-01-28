package net.kigawa.keruta.ktcp.client.queue

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListArg
import net.kigawa.keruta.ktcp.model.queue.list.ServerQueueListEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueListEntrypoint: ServerQueueListEntrypoint<ClientCtx> {
    override fun access(
        input: ServerQueueListArg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input.msg))
        Res.Ok(Unit)
    }
}
