package net.kigawa.keruta.ktcp.client.provider

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.model.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderCompleteEntrypoint : ServerProviderCompleteEntrypoint<ClientCtx> {
    override fun access(
        input: ServerProviderCompleteMsg, ctx: ClientCtx,
    ): EntrypointDeferred<Res<Unit, KtcpClientErr>> = EntrypointDeferred {
        ctx.connection.send(ctx.serializer.serialize(input))
        Res.Ok(Unit)
    }
}
