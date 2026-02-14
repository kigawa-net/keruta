package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.model.provider.add_token.ClientProviderAddTokenEntrypoint
import net.kigawa.keruta.ktcp.model.provider.add_token.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderAddTokenEntrypoint : ClientProviderAddTokenEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderAddTokenMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(
                ctx.serializer.serialize(input)
            )
            Res.Ok(Unit)
        }
    }
}
