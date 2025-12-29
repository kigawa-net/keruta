package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.model.err.GenericErrArg
import net.kigawa.keruta.ktcp.model.err.GenericErrEntrypoint
import net.kigawa.kodel.api.err.Res

class SendGenericErrEntrypoint: GenericErrEntrypoint<ServerCtx> {
    override suspend fun access(
        input: GenericErrArg, ctx: ServerCtx,
    ): Res<Unit, Nothing> {
        ctx.connection.send(ctx.serializer,input)
        return Res.Ok(Unit)
    }
}
