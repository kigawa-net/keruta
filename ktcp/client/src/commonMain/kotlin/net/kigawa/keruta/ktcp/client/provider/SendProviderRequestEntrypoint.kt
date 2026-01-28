package net.kigawa.keruta.ktcp.client.provider

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListArg
import net.kigawa.keruta.ktcp.model.provider.list.ServerProviderListEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderRequestEntrypoint: ServerProviderListEntrypoint<ClientCtx> {
    override fun access(
        input: ServerProviderListArg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input.msg))
        Res.Ok(Unit)
    }
}
