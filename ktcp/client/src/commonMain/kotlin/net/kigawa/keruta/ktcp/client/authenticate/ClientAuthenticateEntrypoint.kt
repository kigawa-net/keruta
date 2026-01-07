package net.kigawa.keruta.ktcp.client.authenticate

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.auth.AuthenticateArg
import net.kigawa.keruta.ktcp.model.auth.AuthenticateEntrypoint
import net.kigawa.kodel.api.err.Res

class ClientAuthenticateEntrypoint: AuthenticateEntrypoint<ClientCtx> {
    override fun access(
        input: AuthenticateArg, ctx: ClientCtx,
    ): Res<Unit, Nothing> {
        ctx.msgSender.sendMsg(input)
        return Res.Ok(Unit)
    }
}
