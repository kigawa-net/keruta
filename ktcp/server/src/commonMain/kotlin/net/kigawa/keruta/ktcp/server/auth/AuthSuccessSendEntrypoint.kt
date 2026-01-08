package net.kigawa.keruta.ktcp.server.auth

import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessArg
import net.kigawa.keruta.ktcp.model.auth.sccess.AuthSuccessEntrypoint
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.entrypoint.EntrypointDeferred
import net.kigawa.kodel.api.err.Res

class AuthSuccessSendEntrypoint: AuthSuccessEntrypoint<ServerCtx> {
    override fun access(
        input: AuthSuccessArg, ctx: ServerCtx,
    ): EntrypointDeferred<in Res<Unit, Nothing>> {
        return EntrypointDeferred {
            ctx.connection.send(ctx.serializer, input.authSuccessMsg)
            Res.Ok(Unit)
        }
    }
}
