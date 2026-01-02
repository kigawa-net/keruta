package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.err.GenericErrEntrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendGenericErrEntrypoint: GenericErrEntrypoint<ServerCtx> {
    override fun access(
        input: GenericErrArg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer, input)
            Res.Ok(Unit)
        }
    }
}
