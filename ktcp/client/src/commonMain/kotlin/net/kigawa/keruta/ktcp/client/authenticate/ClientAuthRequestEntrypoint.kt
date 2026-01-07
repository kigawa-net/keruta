package net.kigawa.keruta.ktcp.client.authenticate

import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestArg
import net.kigawa.keruta.ktcp.model.auth.request.AuthRequestEntrypoint
import net.kigawa.kodel.api.err.Res

class ClientAuthRequestEntrypoint: AuthRequestEntrypoint<ClientCtx> {
    override fun access(
        input: AuthRequestArg, ctx: ClientCtx,
    ): Res<Unit, Nothing> {
        ctx.msgSender.sendMsg(input)
        return Res.Ok(Unit)
    }
}
