package net.kigawa.keruta.ktcp.server.provider

import net.kigawa.keruta.ktcp.domain.provider.addtoken.ClientProviderAddTokenEntrypoint
import net.kigawa.keruta.ktcp.domain.provider.addtoken.ClientProviderAddTokenMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendProviderAddTokenEntrypoint : ClientProviderAddTokenEntrypoint<ServerCtx> {
    override fun access(
        input: ClientProviderAddTokenMsg,
        ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> = EntrypointDeferred {
        ctx.connection.send(
            ctx.serializer.serialize(input),
        )
        Res.Ok(Unit)
    }
}
