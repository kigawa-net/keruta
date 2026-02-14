package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedEntrypoint
import net.kigawa.keruta.ktcp.model.provider.listed.ClientProviderListedMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderListedEntrypoint: ClientProviderListedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderListedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}