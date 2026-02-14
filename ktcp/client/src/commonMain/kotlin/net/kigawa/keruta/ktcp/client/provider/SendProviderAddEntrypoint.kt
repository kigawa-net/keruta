package net.kigawa.keruta.ktcp.client.provider

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddEntrypoint
import net.kigawa.keruta.ktcp.model.provider.add.ServerProviderAddMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderAddEntrypoint : ServerProviderAddEntrypoint<ClientCtx> {
    override fun access(
        input: ServerProviderAddMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input))
        Res.Ok(Unit)
    }
}
