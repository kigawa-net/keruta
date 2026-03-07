package net.kigawa.keruta.ktcp.client.provider

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.err.KtcpClientErr
import net.kigawa.keruta.ktcp.domain.provider.complete.ServerProviderCompleteEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.complete.ServerProviderCompleteMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
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
