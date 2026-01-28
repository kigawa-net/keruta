package net.kigawa.keruta.ktcp.client.queue

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListEntrypoint
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateArg
import net.kigawa.keruta.ktcp.model.queue.create.ServerQueueCreateEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendQueueCreateEntrypoint: ServerQueueCreateEntrypoint<ClientCtx> {
    override fun access(
        input: ServerQueueCreateArg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input.msg))
        Res.Ok(Unit)
    }
}
