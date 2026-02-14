package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.idp_added.ClientProviderIdpAddedEntrypoint
import net.kigawa.keruta.ktcp.model.provider.idp_added.ClientProviderIdpAddedMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderIdpAddedEntrypoint : ClientProviderIdpAddedEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderIdpAddedMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}
