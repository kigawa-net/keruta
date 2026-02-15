package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.deleted.ClientProviderDeletedEntrypoint
import net.kigawa.keruta.ktcp.model.provider.deleted.ClientProviderDeletedMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderDeletedEntrypoint : ClientProviderDeletedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderDeletedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}
