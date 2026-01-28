package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListedArg
import net.kigawa.keruta.ktcp.model.provider.list.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderListedEntrypoint: ClientProviderListedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderListedArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input.msg)
            )
            Res.Ok(Unit)
        }
    }
}
