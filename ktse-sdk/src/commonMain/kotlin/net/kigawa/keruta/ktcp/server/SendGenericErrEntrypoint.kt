package net.kigawa.keruta.ktcp.server

import net.kigawa.keruta.ktcp.domain.err.ClientGenericErrEntrypoint
import net.kigawa.keruta.ktcp.domain.err.GenericErrMsg
import net.kigawa.keruta.ktcp.domain.serialize.serialize
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class SendGenericErrEntrypoint: ClientGenericErrEntrypoint<ServerCtx> {
    override fun access(
        input: GenericErrMsg, ctx: ServerCtx,
    ): EntrypointDeferred<Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer.serialize(input))
            Res.Ok(Unit)
        }
    }
}
