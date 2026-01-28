package net.kigawa.keruta.ktcp.client.queue

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueCreateEntrypoint: ServerQueueCreateEntrypoint<ClientCtx> {
    override fun access(
        input: ServerQueueCreateMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input))
        Res.Ok(Unit)
    }
}
