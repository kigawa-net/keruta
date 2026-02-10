package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedArg
import net.kigawa.keruta.ktcp.model.provider.created.ClientProviderCreatedEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderCreatedEntrypoint: ClientProviderCreatedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderCreatedArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input.msg)
            )
            Res.Ok(Unit)
        }
    }
}
