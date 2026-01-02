package net.kigawa.keruta.ktcp.server.authenticate

import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateArg
import net.kigawa.keruta.ktcp.model.authenticate.AuthenticateEntrypoint
import net.kigawa.keruta.ktcp.server.ServerCtx
import net.kigawa.kodel.api.err.Res

class ReceiveAuthenticateEntrypoint: AuthenticateEntrypoint<ServerCtx> {
    override fun access(
        input: AuthenticateArg, ctx: ServerCtx,
    ): Res<Unit, Nothing> {
        // TODO: トークンの検証ロジックを実装
        ctx.session.authenticated()
        return Res.Ok(Unit)
    }

}
