package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.err.ClientGenericErrArg
import net.kigawa.keruta.ktcp.model.err.ClientGenericErrEntrypoint
import net.kigawa.keruta.ktcp.model.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendGenericErrEntrypoint: ClientGenericErrEntrypoint<ServerCtx> {
    override fun access(
        input: ClientGenericErrArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer.serialize(input.msg))
            Res.Ok(Unit)
        }
    }
}
